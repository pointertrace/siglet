package io.github.pointertrace.siglet.container.eventloop.accumulator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.engine.EngineElement;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.eventloop.EventLoopError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TimeoutAccumulatorEventLoop implements SignalDestination,
        EngineElement {

    private static final Logger LOGGER = LogManager.getLogger(TimeoutAccumulatorEventLoop.class);

    private final String name;

    private final long timeoutInMillis;

    private final int maxSize;

    private final Function<List<Signal>, Signal> accumulatorFunction;

    private final List<SignalDestination> next = new ArrayList<>();

    private final ArrayBlockingQueue<Signal> queue;

    private Thread thread;

    private final AtomicReference<State> state = new AtomicReference<>(State.CREATED);

    private final CountDownLatch stopLatch = new CountDownLatch(1);

    private final CountDownLatch startLatch = new CountDownLatch(1);


    public TimeoutAccumulatorEventLoop(String name, int queueCapacity, int timeoutInMillis, int maxSize,
                                       Function<List<Signal>,Signal> accumulatorFunction) {
        this.name = name;
        this.timeoutInMillis = timeoutInMillis;
        this.maxSize = maxSize;
        this.accumulatorFunction = accumulatorFunction;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.SPAN, SignalType.METRIC);
    }

    @Override
    public boolean send(Signal signal) {
        checkState(State.RUNNING);
        return queue.offer(signal);
    }

    public void connect(SignalDestination destination) {
        if (getState() != State.CREATED) {
            throw new SigletError("Cannot connect if state is not created");
        }
        next.add(destination);
    }

    @Override
    public synchronized void start() {
        checkState(State.CREATED);

        thread = Thread.ofVirtual().name("aggregator-event-loop:" + name).start(() -> {

            Signal signal;
            long nextTick = -1;
            state.set(State.RUNNING);
            startLatch.countDown();
            List<Signal> buffer = new ArrayList<>(maxSize);
            while (true) {

                try {
                    signal = getNextSignal(nextTick);
                } catch (InterruptedException e) {
                    state.set(State.STOPPING);
                    break;
                }

                if (signal != null) {
                    nextTick = processCurrentSignal(buffer, signal, nextTick);
                } else {
                    if (!buffer.isEmpty() && System.nanoTime() >= nextTick) {
                        aggregateAndSend(buffer);
                        nextTick = -1;
                    }
                }
            }
            processRemainingSignals(buffer);
            stopLatch.countDown();
        });

        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new SigletError(String.format("Interrupted exception in event loop %s when waiting threads to start ",
                    name), e);
        }
    }

    private long processCurrentSignal(List<Signal> buffer, Signal signal, long nextTick) {
        buffer.add(signal);
        if (buffer.size() == maxSize) {
            aggregateAndSend(buffer);
            nextTick = -1;
        } else if (buffer.size() == 1) {
            nextTick = System.nanoTime() + timeoutInMillis * 1_000_000L;
        }
        return nextTick;
    }

    private void processRemainingSignals(List<Signal> buffer) {
        while (!queue.isEmpty()) {
            queue.drainTo(buffer, maxSize - buffer.size());
            if (!buffer.isEmpty()) {
                aggregateAndSend(buffer);
            }
        }
    }

    private Signal getNextSignal(long nextTick) throws InterruptedException {
        if (nextTick < 0) {
            return queue.take();
        } else {
            return queue.poll((nextTick - System.nanoTime()) / 1_000_000, TimeUnit.MILLISECONDS);
        }
    }

    private void aggregateAndSend(List<Signal> buffer) {
        try {
            Signal aggregated = accumulatorFunction.apply(buffer);
            for (SignalDestination nextDestination : next) {
                nextDestination.send(aggregated);
            }
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.error("exception aggregating and sending signals {}:{}", buffer, e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public synchronized void stop() {
        checkState(State.RUNNING);

        thread.interrupt();

        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SigletError(String.format("InterrupedException in event loop %s when waiting for threads to " +
                    "start", name));
        }

        state.set(State.STOPPED);

    }

    @Override
    public State getState() {
        return state.get();
    }

    private void checkState(State desiredState) {
        if (state.get() != desiredState) {
            throw new EventLoopError(String.format("state should be %s for this operation but it is %s",
                    desiredState, getState()));
        }
    }
}

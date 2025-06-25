package com.siglet.container.eventloop.accumulator;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.engine.EngineElement;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.eventloop.EventLoopError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TimeoutAccumulatorEventLoop<IN extends Signal, OUT extends Signal> implements SignalDestination<IN>,
        EngineElement {

    private static final Logger LOGGER = LogManager.getLogger(TimeoutAccumulatorEventLoop.class);

    private final String name;

    private final long timeoutInMillis;

    private final int maxSize;

    private final Function<List<IN>, OUT> accumulatorFunction;

    private final List<SignalDestination<OUT>> next = new ArrayList<>();

    private final ArrayBlockingQueue<IN> queue;

    private Thread thread;

    private final AtomicReference<State> state = new AtomicReference<>(State.CREATED);

    private final CountDownLatch stopLatch = new CountDownLatch(1);

    private final CountDownLatch startLatch = new CountDownLatch(1);


    public TimeoutAccumulatorEventLoop(String name, int queueCapacity, int timeoutInMillis, int maxSize,
                                       Function<List<IN>, OUT> accumulatorFunction) {
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
    public Class<IN> getType() {
        return null;
    }

    @Override
    public boolean send(IN signal) {
        checkState(State.RUNNING);
        return queue.offer(signal);
    }

    public void connect(SignalDestination<OUT> destination) {
        if (getState() != State.CREATED) {
            throw new SigletError("Cannot connect if state is not created");
        }
        next.add(destination);
    }

    @Override
    public synchronized void start() {
        checkState(State.CREATED);

        thread = Thread.ofVirtual().name("aggregator-event-loop:" + name).start(() -> {

            IN signal = null;
            long nextTick = -1;
            state.set(State.RUNNING);
            startLatch.countDown();
            List<IN> buffer = new ArrayList<>(maxSize);
            while (true) {

                try {

                    if (nextTick < 0) {
                        signal = queue.take();
                    } else {
                        signal = queue.poll((nextTick - System.nanoTime()) / 1_000_000, TimeUnit.MILLISECONDS);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    state.set(State.STOPPING);
                    break;
                }

                if (signal != null) {
                    buffer.add(signal);
                    if (buffer.size() == maxSize) {
                        aggregateAndSend(buffer);
                        nextTick = -1;
                    } else if (buffer.size() == 1) {
                        nextTick = System.nanoTime() + timeoutInMillis * 1_000_000L;
                    }
                } else {
                    if (!buffer.isEmpty() && System.nanoTime() >= nextTick) {
                        aggregateAndSend(buffer);
                        nextTick = -1;
                    }
                }
            }
            while (!queue.isEmpty()) {
                queue.drainTo(buffer, maxSize - buffer.size());
                if (!buffer.isEmpty()) {
                    aggregateAndSend(buffer);
                }
            }
            stopLatch.countDown();
        });

        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void aggregateAndSend(List<IN> buffer) {
        try {
            OUT aggregated = accumulatorFunction.apply(buffer);
            for (SignalDestination<OUT> next : next) {
                next.send(aggregated);
            }
        } catch (Throwable e) {
            if (e instanceof Error) {
                throw e;
            } else {
                LOGGER.error("exception aggregating and sending signals {}:{}", buffer, e.getMessage(), e);
            }
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
            throw new RuntimeException(e);
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

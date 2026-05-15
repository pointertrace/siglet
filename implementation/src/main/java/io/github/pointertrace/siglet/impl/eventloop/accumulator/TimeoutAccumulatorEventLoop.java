package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.BaseNode;
import io.github.pointertrace.siglet.impl.engine.Component;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.eventloop.EventLoopError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TimeoutAccumulatorEventLoop implements SignalDestination, Component {

    private final String name;

    private final ArrayBlockingQueue<Signal> queue;

    private final int maxSize;

    private final int timeoutInMillis;

    private final Function<Signal[], Signal> accumulator;

    private final List<SignalDestination> destinations;

    private final BufferFactory bufferFactory;

    private Thread thread;

    private final AtomicReference<State> state = new AtomicReference<>(State.CREATED);

    private final CountDownLatch stopLatch = new CountDownLatch(1);

    private final CountDownLatch startLatch = new CountDownLatch(1);


    public TimeoutAccumulatorEventLoop(String name, int queueCapacity, int timeoutInMillis, int maxSize,
                                       Function<Signal[], Signal> accumulator) {
        this(name, queueCapacity, timeoutInMillis, maxSize, accumulator, Buffer::new);
    }

    public TimeoutAccumulatorEventLoop(String name, int queueCapacity, int timeoutInMillis, int maxSize,
                                Function<Signal[], Signal> accumulator, BufferFactory bufferFactory) {
        this.name = name;
        this.maxSize = maxSize;
        this.timeoutInMillis = timeoutInMillis;
        this.accumulator = accumulator;
        this.destinations = new ArrayList<>();
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.bufferFactory = bufferFactory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SignalCapabilities getIncomingCapabilities() {
        return SignalCapabilities.of(Span.class, Metric.class);
    }

    @Override
    public boolean send(Signal signal) {
        requireState(State.RUNNING);
        return queue.offer(signal);
    }

    public void connect(SignalDestination destination) {
        if (getState() != State.CREATED) {
            throw new SigletError("Cannot connect if state is not CREATED");
        }
        destinations.add(destination);
    }

    @Override
    public BaseNode getNode() {
        return null;
    }

    @Override
    public synchronized void start() {
        requireState(State.CREATED);
        thread = Thread.ofVirtual().name("accumulator-event-loop:" + name).start(this::runLoop);
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new SigletError(String.format("Interrupted while waiting for event loop '%s' to start", name), e);
        }
    }

    private void runLoop() {
        state.set(State.RUNNING);
        startLatch.countDown();
        Buffer buffer = bufferFactory.create(maxSize, accumulator, destinations, Deadline.of(timeoutInMillis));
        Signal signal;

        while (true) {
            try {
                signal = getNextSignal(buffer);
            } catch (InterruptedException e) {
                state.set(State.STOPPING);
                break;
            }
            if (signal != null) {
                buffer.add(signal);
            }
        }

        drainRemainingSignals(buffer);
        stopLatch.countDown();
    }

    private Signal getNextSignal(Buffer buffer) throws InterruptedException {
        if (!buffer.hasActiveDeadline()) {
            return queue.take();
        } else {
            return queue.poll(buffer.remainingNanos(), TimeUnit.NANOSECONDS);
        }
    }

    private void drainRemainingSignals(Buffer buffer) {
        List<Signal> remaining = new ArrayList<>();
        queue.drainTo(remaining);
        for (Signal signal : remaining) {
            buffer.add(signal);
        }
        buffer.flush();
    }

    @Override
    public synchronized void stop() {
        requireState(State.RUNNING);
        thread.interrupt();
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SigletError(String.format("Interrupted while waiting for event loop '%s' to stop", name));
        }
        state.set(State.STOPPED);
    }

    @Override
    public State getState() {
        return state.get();
    }

    private void requireState(State expected) {
        if (state.get() != expected) {
            throw new EventLoopError(String.format("Expected state %s but current state is %s", expected, getState()));
        }
    };

    @FunctionalInterface
    public static interface BufferFactory  {
        Buffer create(int maxSize, Function<Signal[], Signal> accumulator, List<SignalDestination> destinations, Deadline deadline);
    }
}

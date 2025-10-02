package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.eventloop.EventLoopError;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessorEventloop<C> implements SignalDestination {

    private static final Logger LOGGER = LogManager.getLogger(ProcessorEventloop.class);

    private final String name;

    private final ProcessorFactory<C> processorFactory;

    private final Context<C> context;

    private final int threadPoolSize;

    private final AtomicReference<State> state = new AtomicReference<>(State.CREATED);

    private final ArrayBlockingQueue<Signal> queue;

    private final List<Thread> threads;

    private final Map<String, String> destinationMappings = new HashMap<>();

    private CountDownLatch stopLatch;

    private List<SignalDestination> next = new ArrayList<>();

    private final SignalType signalType;

    public ProcessorEventloop(String name, ProcessorFactory<C> processorFactory,
                              Context<C> context, SignalType signalType, int queueSize,
                              int threadPoolSize) {
        this(name, processorFactory, context, signalType, queueSize, threadPoolSize, Map.of());
    }

    public ProcessorEventloop(String name, ProcessorFactory<C> processorFactory,
                              Context<C> context, SignalType signalType, int queueSize,
                              int threadPoolSize, Map<String, String> destinationMappings) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name can't be null or empty");
        }
        if (processorFactory == null) {
            throw new IllegalArgumentException("ProcessorFactory can't be null");
        }

        this.name = name;
        this.processorFactory = processorFactory;
        this.context = context;
        this.threadPoolSize = threadPoolSize;
        this.threads = new ArrayList<>(threadPoolSize);
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.signalType = signalType;
        this.destinationMappings.putAll(destinationMappings);
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state.get();
    }

    public boolean send(Signal signal) {
        checkState(State.RUNNING);
        return queue.offer(signal);
    }

    public synchronized void start() {
        checkState(State.CREATED);

        state.set(State.STARTING);
        LOGGER.info("starting event loop {}", name);

        next = Collections.unmodifiableList(next);

        stopLatch = new CountDownLatch(threadPoolSize);
        CountDownLatch startLatch = new CountDownLatch(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            threads.add(Thread.ofVirtual().name("event-loop:" + name + "_" + i).start(() -> processSignals(startLatch)));
        }
        try {
            startLatch.await();
            state.set(State.RUNNING);
            LOGGER.info("event loop {} is running", name);
        } catch (InterruptedException e) {
            throw new EventLoopError(String.format("Interrupted exception in event loop %s when waiting threads to " +
                    "start", name));
        }
    }

    private void processSignals(CountDownLatch startLatch) {

        LOGGER.trace("virtual thread started for event loop {}", name);

        BaseEventloopProcessor<?> baseEventloopProcessor = processorFactory.create(context);

        LOGGER.trace("processor created for event loop {}", name);

        startLatch.countDown();

        while (true) {
            Signal signal = getNextSignal();
            if (signal != null) {
                long starts = System.nanoTime();
                try {
                    long start = System.nanoTime();
                    Result result = baseEventloopProcessor.process(signal);
                    long duration = System.nanoTime() - start;
                    LOGGER.trace("signal {} processed in event loop {} took {} nanos", signal::getId,
                            this::getName, () -> duration);
                    dispatch(result, signal);
                } catch (Error e) {
                    state.set(State.STOPPING);
                    break;
                } catch (Throwable e) {
                    LOGGER.error("exception processing signal {} in event loop {}:{}",
                            signal.getId(), name, e.getMessage(), e);
                }
            } else if (state.get() == State.STOPPING && queue.isEmpty()) {
                LOGGER.trace("getting out of thread loop because state is STOPPING and queue is empty");
                break;
            }
        }
        stopLatch.countDown();
    }

    private void dispatch(Result result, Signal signal) {
        if (result instanceof ResultImpl resultImpl) {
            resultImpl.dispatch(destinationMappings, signal, next);
        } else {
            throw new SigletError(String.format("Result must be type %s but it is %s",
                    ResultImpl.class, result.getClass()));
        }
    }

    private Signal getNextSignal() {
        LOGGER.trace("going to get next signal from queue");
        Signal signal = null;
        try {
            if (state.get() == State.RUNNING) {
                signal = queue.take();
                // TODO ajustr
                LOGGER.trace("state {} got signal from take and it is {}", state.get(), signal.getId());
            } else {
                signal = queue.poll(100, TimeUnit.MILLISECONDS);
                LOGGER.trace("got signal from poll and it is {}", signal == null ? "NULL" : signal.getId());
            }
        } catch (InterruptedException e) {
            state.set(State.STOPPING);
            LOGGER.trace("state set to STOPPING because thread was interrupted");
        }
        return signal;
    }

    public Context<C> getContext() {
        return context;
    }

    public Set<SignalType> getSignalCapabilities() {
        return Set.of(signalType);
    }

    public void connect(SignalDestination signalDestination) {
        checkState(State.CREATED);
        next.add(signalDestination);
    }

    public synchronized void stop() {
        checkState(State.RUNNING);

        state.set(State.STOPPING);
        threads.forEach(Thread::interrupt);
        if (stopLatch != null) {
            try {
                stopLatch.await();
            } catch (InterruptedException e) {
                throw new EventLoopError(String.format("Interrupted exception in event loop %s", name));
            }
        }
        state.set(State.STOPPED);
    }

    private void checkState(State desiredState) {
        if (state.get() != desiredState) {
            throw new EventLoopError(String.format("state should be %s for this operation but it is %s",
                    desiredState, getState()));
        }
    }
}

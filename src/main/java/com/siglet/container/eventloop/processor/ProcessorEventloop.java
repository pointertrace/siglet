package com.siglet.container.eventloop.processor;

import com.siglet.SigletError;
import com.siglet.api.ProcessorContext;
import com.siglet.api.Result;
import com.siglet.api.Signal;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.eventloop.EventLoopError;
import com.siglet.container.eventloop.processor.result.ResultImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessorEventloop<IN extends Signal, CTX> implements SignalDestination<IN> {

    private static final Logger LOGGER = LogManager.getLogger(ProcessorEventloop.class);

    private final String name;

    private final ProcessorFactory<CTX> processorFactory;

    private final ProcessorContext<CTX> processorContext;

    private final int threadPoolSize;

    private final AtomicReference<State> state = new AtomicReference<>(State.CREATED);

    private final ArrayBlockingQueue<IN> queue;

    private final List<Thread> threads;

    private final Map<String, String> destinationMappings = new HashMap<>();

    private CountDownLatch stopLatch;

    private List<SignalDestination<IN>> next = new ArrayList<>();

    private final Class<IN> type;

    public ProcessorEventloop(String name, ProcessorFactory<CTX> processorFactory,
                              ProcessorContext<CTX> processorContext, Class<IN> type, int queueSize,
                              int threadPoolSize) {
        this(name, processorFactory, processorContext, type, queueSize, threadPoolSize, Map.of());
    }

    public ProcessorEventloop(String name, ProcessorFactory<CTX> processorFactory,
                              ProcessorContext<CTX> processorContext, Class<IN> type, int queueSize,
                              int threadPoolSize, Map<String, String> destinationMappings) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name can't be null or empty");
        }
        if (processorFactory == null) {
            throw new IllegalArgumentException("ProcessorFactory can't be null");
        }

        this.name = name;
        this.processorFactory = processorFactory;
        this.processorContext = processorContext;
        this.threadPoolSize = threadPoolSize;
        this.threads = new ArrayList<>(threadPoolSize);
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.type = type;
        this.destinationMappings.putAll(destinationMappings);
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return state.get();
    }

    public boolean send(IN signal) {
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

            threads.add(Thread.ofVirtual().name("event-loop:" + name + "_" + i).start(() -> {

                LOGGER.trace("virtual thread started for event loop {}", name);

                BaseEventloopProcessor<?> baseEventloopProcessor = processorFactory.create(processorContext);

                LOGGER.trace("processor created for event loop {}", name);

                startLatch.countDown();

                while (true) {
                    IN signal = getNextSignal();
                    if (signal != null) {
                        try {
                            long start = System.nanoTime();
                            Result result = baseEventloopProcessor.process(signal);
                            LOGGER.trace("signal {} processed in event loop {} took {} ms", signal::getId,
                                    this::getName, () -> System.nanoTime() - start);
                            if (result instanceof ResultImpl resultImpl) {
                                resultImpl.dispatch(destinationMappings, signal, next);
                            } else {
                                throw new SigletError(String.format("Result must be type %s but it is %s",
                                        ResultImpl.class, result.getClass()));
                            }
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
            }));
        }
        try {
            startLatch.await();
            state.set(State.RUNNING);
            LOGGER.info("event loop {} is running", name);
        } catch (InterruptedException e) {
            throw new EventLoopError(String.format("Interrupted exception in event loop %s", name));
        }
    }

    private IN getNextSignal() {
        LOGGER.trace("going to get next signal from queue");
        IN signal = null;
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

    public ProcessorContext<CTX> getContext() {
        return processorContext;
    }

    public Class<IN> getType() {
        return type;
    }

    public void connect(SignalDestination<IN> signalDestination) {
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
                throw new EventLoopError(String.format("Interrupted exception in event loop %s", name, e));
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

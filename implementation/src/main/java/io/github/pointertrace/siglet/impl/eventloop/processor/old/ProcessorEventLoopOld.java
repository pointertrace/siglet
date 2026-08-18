package io.github.pointertrace.siglet.impl.eventloop.processor.old;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.engine.*;
import io.github.pointertrace.siglet.impl.engine.component.*;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.eventloop.EventLoopError;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProcessorEventLoopOld<C> extends BaseComponent implements SignalSource, SignalDestination {

    private static final Logger LOGGER = LogManager.getLogger(ProcessorEventLoopOld.class);

    private final Component parent;

    private final ProcessorFactory<C> processorFactory;

    private final Context<C> context;

    private final Interceptor interceptor;

    private final int threadPoolSize;

    private final BlockingQueue<Signal> queue;

    private final List<Thread> threads;

    private final Map<String, String> destinationMappings = new HashMap<>();

    private CountDownLatch stopLatch;

    private List<SignalDestination> next = new ArrayList<>();

    private final SignalCapabilities incomingCapabilities;

    private final SignalCapabilities outgoingCapabilities;

    public ProcessorEventLoopOld(Component parent, ProcessorFactory<C> processorFactory,
                                 Context<C> context, SignalCapabilities incomingCapabilities,
                                 SignalCapabilities outgoingCapabilities, Map<String, String> destinationMappings,
                                 int queueSize, int threadPoolSize, Interceptor interceptor) {
        super(interceptor);
        Objects.requireNonNull(parent, "Parent component can't be null or empty");
        Objects.requireNonNull(processorFactory, "ProcessorFactory can't be null");
        this.parent = parent;
        this.interceptor = interceptor;
        this.processorFactory = processorFactory;
        this.context = context;
        this.threadPoolSize = threadPoolSize;
        this.threads = new ArrayList<>(threadPoolSize);
        this.incomingCapabilities = incomingCapabilities;
        this.outgoingCapabilities = outgoingCapabilities;
        this.destinationMappings.putAll(destinationMappings);
//        this.queue = eventBus.eventLoopQueueCreation(this, new ArrayBlockingQueue<>(queueSize));
        this.queue = null;
    }

    public boolean emmit(Signal signal) {
        checkState(State.RUNNING);
        return queue.offer(signal);
    }

    @Override
    public void doStart() {
        next = Collections.unmodifiableList(next);

        stopLatch = new CountDownLatch(threadPoolSize);
        CountDownLatch startLatch = new CountDownLatch(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            threads.add(Thread.ofVirtual().name("event-loop:" + getName() + "_" + i).start(() -> processSignals(startLatch)));
        }
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new EventLoopError(String.format("Interrupted exception in event loop %s when waiting threads to " +
                    "start", getName()));
        }
    }

    @Override
    public synchronized void doStop() {
        threads.forEach(Thread::interrupt);
        if (stopLatch != null) {
            try {
                stopLatch.await();
            } catch (InterruptedException e) {
                throw new EventLoopError(String.format("Interrupted exception in event loop %s", getName()));
            }
        }
    }

    private void processSignals(CountDownLatch startLatch) {

        LOGGER.trace("virtual thread started for event loop {}", getName());

        BaseProcessor<C> sigletProcessor = processorFactory.create(context);

        LOGGER.trace("processor created for event loop {}", getName());

        startLatch.countDown();

        while (true) {
            Signal signal = getNextSignal();
            if (signal != null) {
                try {
                    long start = System.nanoTime();
                    Result result = sigletProcessor.process(signal);
                    long duration = System.nanoTime() - start;
                    // todo ajustar tempo!!!!!
//                    eventBus.eventLoopSigletSignalProcessed(this, signal, duration);
                    LOGGER.trace("signal {} processed in event loop {} took {} nanos", signal::getId,
                            this::getName, () -> duration);
                    dispatch(result, signal);
                } catch (Error e) {
                    requestStop();
                    break;
                } catch (Throwable e) {
                    LOGGER.error("exception processing signal {} in event loop {}:{}",
                            signal.getId(), getName(), e.getMessage(), e);
                }
            } else if (getState() == State.STOPPING && queue.isEmpty()) {
                LOGGER.trace("getting out of thread loop because state is STOPPING and queue is empty");
                break;
            }
        }
        stopLatch.countDown();
    }

    private void dispatch(Result result, Signal signal) {
        if (result instanceof ResultImpl resultImpl) {
//            resultImpl.dispatch(destinationMappings, signal, next);
        } else {
            throw new SigletError(String.format("Result must be type %s but it is %s",
                    ResultImpl.class, result.getClass()));
        }
    }

    private Signal getNextSignal() {
        LOGGER.trace("going to get next signal from queue");
        Signal signal = null;
        try {
            if (getState() == State.RUNNING) {
                signal = queue.take();
                LOGGER.trace("state {} got signal from take and it is {}", getState(), signal.getId());
            } else {
                signal = queue.poll(100, TimeUnit.MILLISECONDS);
                LOGGER.trace("got signal from poll and it is {}", signal == null ? "NULL" : signal.getId());
            }
        } catch (InterruptedException e) {
            requestStop();
            LOGGER.trace("state set to STOPPING because thread was interrupted");
        }
        return signal;
    }

    public Context<C> getContext() {
        return context;
    }


    @Override
    public GraphComponent<?> getGraphComponent() {
        return null;
    }

    @Override
    public void connect(SignalDestination signalDestination) {
        checkState(State.CREATED);
        next.add(signalDestination);
    }

    @Override
    public SignalEmitterFunction getSignalEmitterFunction() {
        return null;
    }

    private void checkState(State desiredState) {
        if (getState() != desiredState) {
            throw new EventLoopError(String.format("state should be %s for this operation but it is %s",
                    desiredState, getState()));
        }
    }

    @Override
    public SignalReceiverFunction getSignalReceiverFunction() {
        return null;
    }

    @Override
    public String getName() {
        return parent.getName() + "-event-loop";
    }

}
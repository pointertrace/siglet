package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EventLoopError;
import io.github.pointertrace.siglet.impl.eventloop.ReceiveFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


public class ProcessorEventLoop<IN, OUT> extends BaseEventLoop<IN, OUT> {

    private static final Logger LOGGER = LogManager.getLogger(ProcessorEventLoop.class);

    private final int threadPoolSize;

    private final BlockingQueue<IN> queue;

    private final List<Thread> threads;

    private CountDownLatch stopLatch;

    /**
     * Factory called once per pool thread to produce an isolated function instance.
     */
    private final Supplier<Function<IN, OUT>> processFunctionFactory;

    public ProcessorEventLoop(Component parent,
                              int queueSize,
                              int threadPoolSize,
                              Interceptor interceptor,
                              Supplier<Function<IN, OUT>> processFunctionFactory,
                              EmitterFunction<OUT> signalEmitterFunction) {
        super(parent, "processor-event-loop", signalEmitterFunction, interceptor);
        this.threadPoolSize = threadPoolSize;
        this.threads = new ArrayList<>(threadPoolSize);
        this.queue = interceptor.eventLoopQueueCreation(this, new ArrayBlockingQueue<>(queueSize));
        this.processFunctionFactory = processFunctionFactory;
    }

    private boolean receive(IN request) {
        return queue.offer(request);
    }

    @Override
    public void doStart() {
        stopLatch = new CountDownLatch(threadPoolSize);
        CountDownLatch startLatch = new CountDownLatch(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            threads.add(Thread.ofVirtual().name("event-loop:" + getName() + "_" + i).start(() -> processBatches(startLatch)));
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

    private void processBatches(CountDownLatch startLatch) {

        LOGGER.trace("virtual thread started for event loop {}", getName());

        Function<IN, OUT> processFunction =
                getEventBus().eventLoopProcessFunctionCreation(this, processFunctionFactory.get());

        LOGGER.trace("processor created for event loop {}", getName());

        startLatch.countDown();

        while (true) {
            IN in = getNext();
            if (in != null) {
                try {
                    emit(processFunction.apply(in));
                } catch (Error e) {
                    requestStop();
                    break;
                } catch (Throwable e) {
                    LOGGER.error("exception processing element type {} in event loop {}:{}",
                            in.getClass().getName(), getName(), e.getMessage(), e);
                }
            } else if (getState() == State.STOPPING && queue.isEmpty()) {
                LOGGER.trace("getting out of thread loop because state is STOPPING and queue is empty");
                break;
            }
        }
        stopLatch.countDown();
    }

    private IN getNext() {
        LOGGER.trace("going to get next element from queue");
        IN in = null;
        try {
            if (getState() == State.RUNNING) {
                in = queue.take();
            } else {
                in = queue.poll(100, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            requestStop();
            LOGGER.trace("state set to STOPPING because thread was interrupted");
        }
        return in;
    }


    @Override
    public ReceiveFunction<IN> getReceiver() {
        return this::receive;
    }
}

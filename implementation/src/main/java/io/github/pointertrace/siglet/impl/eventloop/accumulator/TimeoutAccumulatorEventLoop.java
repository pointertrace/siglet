package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.event.EventBus;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.ReceiveFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TimeoutAccumulatorEventLoop<IN, OUT> extends BaseEventLoop<IN, OUT> {

    private final BlockingQueue<IN> queue;

    private final int maxSize;

    private final int timeoutInMillis;

    private final Class<IN> inClass;

    private final Function<IN[], OUT> transformerFunction;

    private final EmitterFunction<OUT> signalEmitterFunction;

    private final BufferFactory bufferFactory = Buffer::new;

    private Thread thread;

    private final CountDownLatch stopLatch = new CountDownLatch(1);

    private final CountDownLatch startLatch = new CountDownLatch(1);

    public TimeoutAccumulatorEventLoop(Component parent, String name, int queueCapacity, int timeoutInMillis, int maxSize,
                                       Class<IN> inClass, Function<IN[], OUT> transformerFunction, EmitterFunction<OUT> signalEmitterFunction, EventBus eventBus) {
        super(parent, name, signalEmitterFunction, eventBus);
        this.timeoutInMillis = timeoutInMillis;
        this.maxSize = maxSize;
        this.inClass = inClass;
        this.transformerFunction = transformerFunction;
        this.signalEmitterFunction = signalEmitterFunction;
        this.queue = eventBus.eventLoopQueueCreation(this, new ArrayBlockingQueue<>(queueCapacity));
    }


    public boolean receive(IN signal) {
        return queue.offer(signal);
    }

    @Override
    public void doStart() {
        thread = Thread.ofVirtual().name(getName()).start(this::runLoop);
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new SigletError(String.format("Interrupted while waiting for event loop '%s' to start", getName()), e);
        }
    }

    private void runLoop() {
        startLatch.countDown();
        Buffer<IN, OUT> buffer = bufferFactory.create(maxSize, Deadline.of(timeoutInMillis), inClass,
                transformerFunction, this::receiveFromBuffer);
        IN in;

        while (true) {
            try {
                in = getNext(buffer);
            } catch (InterruptedException e) {
                break;
            }
            if (in != null) {
                buffer.add(in);
            } else if (buffer.hasActiveDeadline()) {
                buffer.flush();
            }
        }

        drainRemainingSignals(buffer);

        stopLatch.countDown();
    }

    private IN getNext(Buffer<IN, OUT> buffer) throws InterruptedException {
        if (!buffer.hasActiveDeadline()) {
            return queue.take();
        } else {
            return queue.poll(buffer.remainingNanos(), TimeUnit.NANOSECONDS);
        }
    }

    private void drainRemainingSignals(Buffer<IN, OUT> buffer) {
        List<IN> remaining = new ArrayList<>();
        queue.drainTo(remaining);
        for (IN in : remaining) {
            buffer.add(in);
        }
        buffer.flush();
    }

    @Override
    public void doStop() {
        thread.interrupt();
        try {
            stopLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SigletError(String.format("Interrupted while waiting for event loop '%s' to stop",getName()));
        }
    }

    private void receiveFromBuffer(OUT out) {
       signalEmitterFunction.emit(out);
    }


    @FunctionalInterface
    public interface BufferFactory {
        <IN, OUT> Buffer<IN, OUT> create(int maxSize, Deadline deadline, Class<IN> inClass,
                                         Function<IN[], OUT> transformerFunction, EmitterFunction<OUT> signalEmitterFunction);
    }
}

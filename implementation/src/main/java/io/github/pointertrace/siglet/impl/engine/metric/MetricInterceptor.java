package io.github.pointertrace.siglet.impl.engine.metric;

import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MetricInterceptor implements Interceptor {



    private final Metrics metrics;

    public MetricInterceptor(Metrics metrics) {
        this.metrics = metrics;
    }


    @Override
    public Component componentInstantiation(Component component) {
        return component;
    }

    @Override
    public <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?, ?> eventLoop,
                                                       BlockingQueue<T> eventLoopQueue) {

        MeteredBlockingQueue<T> meteredBlockingQueue = new MeteredBlockingQueue<>(
                eventLoopQueue,
                metrics.createQueueWaitTimer(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                metrics.createQueueReceivedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                metrics.createQueueAcceptedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName()),
                metrics.createQueueMissedSignalsCounter(eventLoop.getParentComponent().getName(), eventLoop.getName())
        );

        metrics.createQueueMetrics(eventLoop.getParentComponent().getName(), eventLoop.getName(), meteredBlockingQueue);
        return meteredBlockingQueue;
    }

    @Override
    public <T> EmitterFunction<T> eventLoopEmitterFunctionCreation(BaseEventLoop<?, ?> eventLoop,
                                                                   EmitterFunction<T> signalEmitterFunction) {

        LongCounter emitted = metrics.createEmittedSignalsCounter(eventLoop.getName());
        return (T signal) -> {
            emitted.increment();
            signalEmitterFunction.emit(signal);
        };
    }

    @Override
    public <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(
            BaseEventLoop<?, ?> eventLoop, Function<IN, OUT> processFunction) {

        LongTimer longTimer = metrics.createEventLoopProcessFunctionTimer(eventLoop.getParentComponent().getName(), eventLoop.getName());
        return (IN in) -> {
            long start = System.nanoTime();
            try {
                return processFunction.apply(in);
            } finally {
                longTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
        };
    }

    @Override
    public SignalReceiverFunction componentReceiverFunctionCreation(
            Component component, SignalReceiverFunction signalReceiverFunction) {
        LongCounter received = metrics.createReceivedSignalsCounter(component.getName());
        return (signal -> {
            received.increment();
            return signalReceiverFunction.receive(signal);
        });
    }

    @Override
    public SignalReceiverFunction componentEmitterFunctionCreation(
            Component source, Component destination, SignalReceiverFunction signalReceiverFunction) {
        LongCounter emitted = metrics.createEmittedSignalsCounter(source.getName());
        LongCounter accepted = metrics.createAcceptedSignalsCounter(source.getName(), destination.getName());
        LongCounter missed = metrics.createMissedSignalsCounter(source.getName(), destination.getName());
        return (signal) -> {
            emitted.increment();
            boolean acceptedSignal = signalReceiverFunction.receive(signal);
            if (acceptedSignal) {
                accepted.increment();
            } else {
                missed.increment();
            }
            return acceptedSignal;
        };
    }

    @Override
    public SignalReceiverFunction componentEmitterDropFunctionCreation(Component source) {
        LongCounter dropped = metrics.createDroppedSignalsCounter(source.getName());
        return (signal) -> {
            dropped.increment();
            return true;
        };
    }


}

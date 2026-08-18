package io.github.pointertrace.siglet.impl.engine.event;

import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public interface EventBus {

    void componentAfterInstantiation(Component component);

    void componentBeforeStart(Component component);

    void componentAfterStart(Component component);

    void componentBeforeStop(Component component);

    void componentAfterStop(Component component);

    void signalExportedAccepted(Exporter exporter, long numAcceptedExportedSignals);

    void signalExportedMissed(Exporter exporter, long numMissedExportedSignals);

    <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?,?> eventLoop, BlockingQueue<T> eventLoopQueue);

    <T> EmitterFunction<T> eventLoopEmitFunctionCreation(BaseEventLoop<?,?> eventLoop, EmitterFunction<T> signalEmitterFunction);

    <T> SignalDestination beforeConnect(SignalSource signalSource, SignalDestination signalDestination);

    void addEventListener(EventListener eventListener);

    <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(BaseEventLoop<?,?> eventLoop, Function<IN, OUT> processFunction);
}

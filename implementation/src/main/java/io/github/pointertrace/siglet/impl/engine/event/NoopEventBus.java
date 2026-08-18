package io.github.pointertrace.siglet.impl.engine.event;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class NoopEventBus implements EventBus {

    @Override
    public void componentAfterInstantiation(Component component) {
    }

    @Override
    public void componentBeforeStart(Component component) {
    }

    @Override
    public void componentAfterStart(Component component) {
    }

    @Override
    public void componentBeforeStop(Component component) {
    }

    @Override
    public void componentAfterStop(Component component) {
    }

    @Override
    public void signalExportedAccepted(Exporter exporter, long numAcceptedExportedSignals) {
    }

    @Override
    public void signalExportedMissed(Exporter exporter, long numMissedExportedSignals) {
    }

    @Override
    public <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?,?> eventLoop, BlockingQueue<T> eventLoopQueue) {
        return eventLoopQueue;
    }

    @Override
    public <T> EmitterFunction<T> eventLoopEmitFunctionCreation(BaseEventLoop<?,?> eventLoop, EmitterFunction<T> signalEmitterFunction) {
        return signalEmitterFunction;
    }

    @Override
    public SignalDestination beforeConnect(SignalSource signalSource, SignalDestination signalDestination) {
        return signalDestination;
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        throw new SigletError("Could not add a event listener to a NoopEventBus");
    }

    @Override
    public <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(BaseEventLoop<?,?> eventLoop, Function<IN, OUT> processFunction) {
        return processFunction;
    }

}

package io.github.pointertrace.siglet.impl.engine.interceptor;

import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Interceptor {

    Component componentInstantiation(Component component);

    <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?, ?> eventLoop, BlockingQueue<T> eventLoopQueue);

    <T> EmitterFunction<T> eventLoopEmitterFunctionCreation(BaseEventLoop<?, ?> eventLoop, EmitterFunction<T> signalEmitterFunction);

    <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(BaseEventLoop<?, ?> eventLoop, Function<IN, OUT> processFunction);

    SignalReceiverFunction componentReceiverFunctionCreation(Component component, SignalReceiverFunction signalReceiverFunction);


    SignalReceiverFunction componentEmitterFunctionCreation(
            Component source, Component destination, SignalReceiverFunction signalReceiverFunction);

    SignalReceiverFunction componentEmitterDropFunctionCreation(Component source);

}

package io.github.pointertrace.siglet.impl.engine.event.eventloop;

import io.github.pointertrace.siglet.impl.engine.event.EventListener;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public interface EventLoopEventListener extends EventListener {

    <T> BlockingQueue<T> eventLoopQueueCreation(long timestamp, BaseEventLoop<?,?> eventLoop, BlockingQueue<T> eventLoopQueue);

    <T> EmitterFunction<T> eventLoopEmitFunctionCreation(long timestamp, BaseEventLoop<?,?> eventLoop, EmitterFunction<T> signalEmitterFunction);

    <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(long timestamp, BaseEventLoop<?,?> eventLoop, Function<IN, OUT> processFunction);

}

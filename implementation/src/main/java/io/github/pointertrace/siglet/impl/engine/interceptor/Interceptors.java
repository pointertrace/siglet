package io.github.pointertrace.siglet.impl.engine.interceptor;

import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalReceiverFunction;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class Interceptors implements Interceptor {

    private final List<Interceptor> interceptors = new ArrayList<>();


    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Override
    public Component componentInstantiation(Component component) {
        Component current = component;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.componentInstantiation(current);
        }
        return current;
    }

    @Override
    public <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?, ?> eventLoop, BlockingQueue<T> eventLoopQueue) {
        BlockingQueue<T> current = eventLoopQueue;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.eventLoopQueueCreation(eventLoop, current);
        }
        return current;
    }

    @Override
    public <T> EmitterFunction<T> eventLoopEmitterFunctionCreation(BaseEventLoop<?, ?> eventLoop, EmitterFunction<T> signalEmitterFunction) {
        EmitterFunction<T> current = signalEmitterFunction;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.eventLoopEmitterFunctionCreation(eventLoop, current);
        }
        return current;
    }

    @Override
    public <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(BaseEventLoop<?, ?> eventLoop, Function<IN, OUT> processFunction) {
        Function<IN, OUT> current = processFunction;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.eventLoopProcessFunctionCreation(eventLoop, current);
        }
        return current;
    }

    @Override
    public SignalReceiverFunction componentReceiverFunctionCreation(Component component, SignalReceiverFunction signalReceiverFunction) {
        SignalReceiverFunction current = signalReceiverFunction;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.componentReceiverFunctionCreation(component, current);
        }
        return current;
    }

    @Override
    public SignalReceiverFunction componentEmitterFunctionCreation(Component source, Component destination, SignalReceiverFunction signalReceiverFunction) {
        SignalReceiverFunction current = signalReceiverFunction;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.componentEmitterFunctionCreation(source, destination, current);
        }
        return current;
    }

    @Override
    public SignalReceiverFunction componentEmitterDropFunctionCreation(Component source) {
        SignalReceiverFunction current = (signal) -> true;
        for (Interceptor interceptor : interceptors) {
            current = interceptor.componentEmitterDropFunctionCreation(source);
        }
        return current;
    }
}

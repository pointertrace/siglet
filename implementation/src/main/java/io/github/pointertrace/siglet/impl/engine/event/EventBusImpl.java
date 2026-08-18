package io.github.pointertrace.siglet.impl.engine.event;

import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.event.componentlifecycle.ComponentLifeCycleEventListener;
import io.github.pointertrace.siglet.impl.engine.event.connection.ConnectionEventListener;
import io.github.pointertrace.siglet.impl.engine.event.eventloop.EventLoopEventListener;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.eventloop.BaseEventLoop;
import io.github.pointertrace.siglet.impl.eventloop.EmitterFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class EventBusImpl implements EventBus {

    private final List<EventListener> eventListeners = new ArrayList<>();

    @Override
    public void componentAfterInstantiation(Component component) {
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ComponentLifeCycleEventListener componentLifeCycleEventListener) {
                componentLifeCycleEventListener
                        .afterInstantiation(System.currentTimeMillis(),component);
            }
        }
    }

    @Override
    public void componentBeforeStart(Component component) {
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ComponentLifeCycleEventListener componentLifeCycleEventListener) {
                componentLifeCycleEventListener
                        .beforeStart(System.currentTimeMillis(),component);
            }
        }
    }

    @Override
    public void componentAfterStart(Component component) {
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ComponentLifeCycleEventListener componentLifeCycleEventListener) {
                componentLifeCycleEventListener
                        .afterStart(System.currentTimeMillis(),component);
            }
        }
    }

    @Override
    public void componentBeforeStop(Component component) {
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ComponentLifeCycleEventListener componentLifeCycleEventListener) {
                componentLifeCycleEventListener
                        .beforeEnd(System.currentTimeMillis(),component);
            }
        }
    }

    @Override
    public void componentAfterStop(Component component) {
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ComponentLifeCycleEventListener componentLifeCycleEventListener) {
                componentLifeCycleEventListener
                        .afterEnd(System.currentTimeMillis(),component);
            }
        }
    }

    @Override
    public void signalExportedAccepted(Exporter exporter, long numAcceptedExportedSignals) {

    }

    @Override
    public void signalExportedMissed(Exporter exporter, long numMissedExportedSignals) {

    }

    @Override
    public <T> BlockingQueue<T> eventLoopQueueCreation(BaseEventLoop<?,?> eventLoop, BlockingQueue<T> eventLoopQueue) {
        BlockingQueue<T>  current = eventLoopQueue;
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof EventLoopEventListener eventLoopEventListener) {
                current =  eventLoopEventListener.eventLoopQueueCreation(System.currentTimeMillis(),eventLoop, current);
            }
        }
        return current;
    }

    @Override
    public <T> EmitterFunction<T> eventLoopEmitFunctionCreation(BaseEventLoop<?,?> eventLoop, EmitterFunction<T> signalEmitterFunction) {
        EmitterFunction<T> current = signalEmitterFunction;
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof EventLoopEventListener eventLoopEventListener) {
                current =  eventLoopEventListener.eventLoopEmitFunctionCreation(System.currentTimeMillis(),eventLoop, current);
            }
        }
        return current;
    }

    @Override
    public <IN, OUT> Function<IN, OUT> eventLoopProcessFunctionCreation(BaseEventLoop<?,?> eventLoop, Function<IN, OUT> processFunction) {
        Function<IN,OUT> current = processFunction;
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof EventLoopEventListener eventLoopEventListener) {
                current =  eventLoopEventListener.eventLoopProcessFunctionCreation(System.currentTimeMillis(),eventLoop, current);
            }
        }
        return current;
    }

    @Override
    public SignalDestination beforeConnect(SignalSource signalSource, SignalDestination signalDestination) {
        SignalDestination  current = signalDestination;
        for(EventListener eventListener: eventListeners) {
            if (eventListener instanceof ConnectionEventListener connectionEventListener) {
                current =  connectionEventListener.beforeConnect(signalSource,current);
            }
        }
        return current;
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
    }


}

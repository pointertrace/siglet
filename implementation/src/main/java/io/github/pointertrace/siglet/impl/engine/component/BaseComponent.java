package io.github.pointertrace.siglet.impl.engine.component;

import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.event.EventBus;
import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseComponent implements Component {

    private final AtomicReference<State> state;

    private final EventBus eventBus;

    public BaseComponent(EventBus eventBus) {
        this.eventBus = eventBus;
        this.state = new AtomicReference<>(State.CREATED);
    }

    @Override
    public final void start() {
        eventBus.componentBeforeStart(this);
        state.set(State.STARTING);
        doStart();
        state.set(State.RUNNING);
        eventBus.componentAfterStart(this);
    }

    @Override
    public final void stop() {
        eventBus.componentBeforeStop(this);
        state.set(State.STOPPING);
        doStop();
        state.set(State.STOPPED);
        eventBus.componentAfterStop(this);
    }

    @Override
    public State getState() {
        return state.get();
    }

    public void requestStop() {
        state.set(State.STOPPING);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    protected abstract void doStart();
    protected abstract void doStop();

}

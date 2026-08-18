package io.github.pointertrace.siglet.impl.engine.component;

import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseComponent implements Component {

    private final AtomicReference<State> state;

    private final Interceptor interceptor;

    public BaseComponent(Interceptor interceptor) {
        this.interceptor = interceptor;
        this.state = new AtomicReference<>(State.CREATED);
    }

    @Override
    public final void start() {
        state.set(State.STARTING);
        doStart();
        state.set(State.RUNNING);
    }

    @Override
    public final void stop() {
        state.set(State.STOPPING);
        doStop();
        state.set(State.STOPPED);
    }

    @Override
    public State getState() {
        return state.get();
    }

    public void requestStop() {
        state.set(State.STOPPING);
    }

    public Interceptor getEventBus() {
        return interceptor;
    }

    protected abstract void doStart();
    protected abstract void doStop();

}

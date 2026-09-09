package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.impl.engine.component.BaseComponent;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.interceptor.Interceptor;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class BaseEventLoop<IN, OUT> extends BaseComponent {

    private final Component parent;

    private final String name;

    private final EmitterFunction<OUT> emitterFunction;

    protected BaseEventLoop(Component parent, String name, EmitterFunction<OUT> emitterFunction, Interceptor interceptor) {
        super(interceptor);
        this.parent = parent;
        this.name = name;
        this.emitterFunction = emitterFunction;
    }

    public abstract ReceiveFunction<IN> getReceiver();

    protected void emit(OUT out) {
        emitterFunction.emit(out);
    }

    public String getName() {
        return name;
    }

    public Component getParentComponent() {
        return parent;
    }


}

package io.github.pointertrace.siglet.impl.eventloop;

import io.github.pointertrace.siglet.impl.engine.component.BaseComponent;
import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.event.EventBus;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class BaseEventLoop<IN, OUT> extends BaseComponent {

    private final Component parent;

    private final String nameSuffix;

    private final EmitterFunction<OUT> signalEmitterFunction;

    protected BaseEventLoop(Component parent, String nameSuffix, EmitterFunction<OUT> signalEmitterFunction, EventBus eventBus) {
        super(eventBus);
        this.parent = parent;
        this.nameSuffix = nameSuffix;
        this.signalEmitterFunction = signalEmitterFunction;
    }

    public abstract boolean receive(IN in);

    protected void emit(OUT out) {
        signalEmitterFunction.emit(out);
    }

    public String getName() {
        return Stream.of(parent.getName(), nameSuffix, "event-loop")
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .reduce((a, b) -> a + "-" + b).orElse("");
    }

    public Component getParentComponent() {
        return parent;
    }


}

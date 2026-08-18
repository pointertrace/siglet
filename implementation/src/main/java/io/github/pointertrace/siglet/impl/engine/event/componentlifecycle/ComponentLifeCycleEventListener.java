package io.github.pointertrace.siglet.impl.engine.event.componentlifecycle;

import io.github.pointertrace.siglet.impl.engine.component.Component;
import io.github.pointertrace.siglet.impl.engine.event.EventListener;

public interface ComponentLifeCycleEventListener extends EventListener {

    void afterInstantiation(long timestamp, Component component);

    void beforeStart(long timestamp, Component component);

    void afterStart(long timestamp, Component component);

    void beforeEnd(long timestamp, Component component);

    void afterEnd(long timestamp, Component component);

}

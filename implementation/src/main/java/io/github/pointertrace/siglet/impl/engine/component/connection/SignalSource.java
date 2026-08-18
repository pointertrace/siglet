package io.github.pointertrace.siglet.impl.engine.component.connection;

import io.github.pointertrace.siglet.impl.engine.component.GraphComponent;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;

public interface SignalSource {

    GraphComponent<?> getGraphComponent();

    void connect(SignalDestination destination);

    SignalEmitterFunction getSignalEmitterFunction();

}

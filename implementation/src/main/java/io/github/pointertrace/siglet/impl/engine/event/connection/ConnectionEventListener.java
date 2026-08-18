package io.github.pointertrace.siglet.impl.engine.event.connection;

import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;

public interface ConnectionEventListener {

    SignalDestination beforeConnect(SignalSource signalSource, SignalDestination signalDestination);

}

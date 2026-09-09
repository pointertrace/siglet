package io.github.pointertrace.siglet.impl.engine.component.connection;

public interface SignalDestinationProvider {

    String getName();

    SignalDestination getSignalDestination();
}

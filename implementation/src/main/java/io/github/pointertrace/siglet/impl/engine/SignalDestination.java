package io.github.pointertrace.siglet.impl.engine;

import io.github.pointertrace.siglet.api.Signal;

public interface SignalDestination {

    String getName();

    boolean send(Signal signal);

    SignalCapabilities getIncomingCapabilities();

}

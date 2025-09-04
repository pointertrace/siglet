package io.github.pointertrace.siglet.container.engine;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.SignalType;

import java.util.Set;

public interface SignalDestination {

    String getName();

    boolean send(Signal signal);

    Set<SignalType> getSignalCapabilities();

}

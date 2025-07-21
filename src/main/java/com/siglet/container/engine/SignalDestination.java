package com.siglet.container.engine;

import com.siglet.api.Signal;
import com.siglet.container.config.raw.SignalType;

import java.util.Set;

public interface SignalDestination {

    String getName();

    boolean send(Signal signal);

    Set<SignalType> getSignalCapabilities();

}

package com.siglet.container.engine;

import com.siglet.api.Signal;

public interface SignalDestination<T extends Signal> {

    String getName();

    boolean send(T signal);

    Class<T> getType();

}

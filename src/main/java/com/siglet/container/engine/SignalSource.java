package com.siglet.container.engine;

import com.siglet.api.Signal;

public interface SignalSource<T extends Signal> {

    void connect(SignalDestination<T> destination);
    
}

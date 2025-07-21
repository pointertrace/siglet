package com.siglet.container.engine;

import com.siglet.api.Signal;

public interface SignalSource {

    void connect(SignalDestination destination);
    
}

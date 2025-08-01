package io.github.pointertrace.siglet.container.engine;

public interface SignalSource {

    void connect(SignalDestination destination);
    
}

package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;

public class BaseProxy {

    private final Signal signal;

    public BaseProxy(Signal signal) {
        this.signal = signal;
    }


    public final Signal getSignal() {
        return signal;
    }

}

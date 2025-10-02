package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;

public class BaseProxy {

    private final Signal signal;

    public BaseProxy(Signal signal) {
        this.signal = signal;
    }


    public final Signal getSignal() {
        return signal;
    }

}

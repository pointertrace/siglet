package com.siglet.pipeline.common.processor.groovy.proxy;

public class BaseProxy {

    private final Object thisSignal;

    public BaseProxy(Object thisSignal) {
        this.thisSignal = thisSignal;
    }


    public final Object getThisSignal() {
        return thisSignal;
    }

}

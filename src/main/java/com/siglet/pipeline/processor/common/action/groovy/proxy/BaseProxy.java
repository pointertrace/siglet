package com.siglet.pipeline.processor.common.action.groovy.proxy;

public class BaseProxy {

    private final Object thisSignal;

    public BaseProxy(Object thisSignal) {
        this.thisSignal = thisSignal;
    }


    public final Object getThisSignal() {
        return thisSignal;
    }

}

package com.siglet.container.config.graph;

import com.siglet.api.Signal;
import com.siglet.container.config.raw.OtelSignalType;

public enum SignalType {

    SPAN(OtelSignalType.TRACE),
    TRACE(OtelSignalType.TRACE),
    METRIC(OtelSignalType.METRIC),
    SIGNAL(Signal.class);

    private OtelSignalType otelSignalType;

    private final Class<? extends Signal> baseType;

    SignalType(OtelSignalType otelSignalType) {
        this.otelSignalType = otelSignalType;
        this.baseType = otelSignalType.getBaseType();
    }

    SignalType(Class<Signal> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends Signal> getBaseType() {
        return otelSignalType.getBaseType();
    }

    public boolean isCompatible(Signal signal) {
        return baseType.isAssignableFrom(signal.getClass());
    }
}

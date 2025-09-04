package io.github.pointertrace.siglet.container.config.graph;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.raw.OtelSignalType;

import java.util.Arrays;

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

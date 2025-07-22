package com.siglet.container.config.raw;


import com.siglet.api.Signal;
import com.siglet.api.signal.metric.Metric;
import com.siglet.api.signal.trace.Span;

public enum OtelSignalType {

    TRACE(Span.class),
    METRIC(Metric.class);

    private final Class<? extends com.siglet.api.Signal> baseType;

    OtelSignalType(Class<? extends com.siglet.api.Signal> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends com.siglet.api.Signal> getBaseType() {
        return baseType;
    }

    public boolean isCompatible(Signal signal) {
        return baseType.isAssignableFrom(signal.getClass());
    }
}

package com.siglet.container.config.raw;


import com.siglet.api.Signal;
import com.siglet.api.signal.metric.Metric;
import com.siglet.api.signal.trace.Span;

public enum SignalType {

    TRACE(Span.class, false),
    METRIC(Metric.class, false),
    SIGNAL(Signal.class, true);

    private final Class<? extends com.siglet.api.Signal> baseType;

    private final boolean internal;

    SignalType(Class<? extends com.siglet.api.Signal> baseType, boolean internal) {
        this.baseType = baseType;
        this.internal = internal;
    }

    public Class<? extends com.siglet.api.Signal> getBaseType() {
        return baseType;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isCompatible(Signal signal) {
        return baseType.isAssignableFrom(signal.getClass());
    }
}

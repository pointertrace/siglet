package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;

public enum OtelSignalType {

    TRACE(Span.class),
    METRIC(Metric.class);

    private final Class<? extends io.github.pointertrace.siglet.api.Signal> baseType;

    OtelSignalType(Class<? extends io.github.pointertrace.siglet.api.Signal> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends io.github.pointertrace.siglet.api.Signal> getBaseType() {
        return baseType;
    }

    public boolean isCompatible(Signal signal) {
        return baseType.isAssignableFrom(signal.getClass());
    }
}

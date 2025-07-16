package com.siglet.container.config.raw;


import com.siglet.api.data.metric.Metric;
import com.siglet.api.data.trace.Span;

public enum Signal {

    TRACE(Span.class),
    METRIC(Metric.class);

    private final Class<? extends com.siglet.api.Signal> baseType;

    Signal(Class<? extends com.siglet.api.Signal> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends com.siglet.api.Signal> getBaseType() {
        return baseType;
    }


}

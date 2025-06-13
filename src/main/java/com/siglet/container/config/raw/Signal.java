package com.siglet.container.config.raw;

import com.siglet.api.unmodifiable.metric.UnmodifiableMetric;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;

public enum Signal {

    TRACE(UnmodifiableSpan.class),
    METRIC(UnmodifiableMetric.class);

    private final Class<? extends com.siglet.api.Signal> baseType;

    Signal(Class<? extends com.siglet.api.Signal> baseType) {
        this.baseType = baseType;
    }

    public Class<? extends com.siglet.api.Signal> getBaseType() {
        return baseType;
    }


}

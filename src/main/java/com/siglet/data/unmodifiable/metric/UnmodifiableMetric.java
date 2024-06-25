package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableMetric {

    String getName();

    String getDescription();

    String getUnit();

    UnmodifiableData getData();

    boolean hasGauge();

    UnmodifiableGauge getGauge();
}

package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableMetric {

    String getName();

    String getDescription();

    String getUnit();

    UnmodifiableData getData();

    boolean hasGauge();

    UnmodifiableGauge getGauge();

    boolean hasSum();

    UnmodifiableSum getSum();

    boolean hasHistogram();

    UnmodifiableHistogram getHistogram();

    boolean hasExponentialHistogram();

    UnmodifiableExponentialHistogram getExponentialHistogram();

    boolean hasSummary();

    UnmodifiableSummary getSummary();
}

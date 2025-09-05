package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.Signal;

public interface Metric extends Signal {

    String getName();

    Metric setName(String name);

    String getDescription();

    Metric setDescription(String description);

    String getUnit();

    Metric setUnit(String unit);

    Data getData();

    boolean hasGauge();

    Gauge getGauge();

    boolean hasSum();

    Sum getSum();

    boolean hasHistogram();

    Histogram getHistogram();

    boolean hasExponentialHistogram();

    ExponentialHistogram getExponentialHistogram();

    boolean hasSummary();

    Summary getSummary();
}

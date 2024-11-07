package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableMetric;

public interface ModifiableMetric extends UnmodifiableMetric {

    ModifiableMetric setName(String name);

    ModifiableMetric setDescription(String description);

    ModifiableMetric setUnit(String unit);

    ModifiableData getData();

    ModifiableGauge getGauge();

    ModifiableSum getSum();

    ModifiableHistogram getHistogram();

    ModifiableExponentialHistogram getExponentialHistogram();

    ModifiableSummary getSummary();
}

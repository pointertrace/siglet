package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableData;
import com.siglet.data.unmodifiable.metric.UnmodifiableMetric;

public interface ModifiableMetric extends UnmodifiableMetric {

    void setName(String name);

    void setDescription(String description);

    void setUnit(String unit);

    ModifiableData getData();
}

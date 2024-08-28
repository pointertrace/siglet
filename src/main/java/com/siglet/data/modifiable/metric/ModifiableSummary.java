package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableSummary;

public interface ModifiableSummary extends UnmodifiableSummary {

    ModifiableSummaryDataPoints getDataPoints();

}

package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableSummary;

public interface ModifiableSummary extends  ModifiableData,UnmodifiableSummary {

    ModifiableSummaryDataPoints getDataPoints();

}

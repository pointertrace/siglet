package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableSummaryDataPoints;

public interface ModifiableSummaryDataPoints extends UnmodifiableSummaryDataPoints {

    ModifiableSummaryDataPoint getAt(int i);

    void remove(int i);
}

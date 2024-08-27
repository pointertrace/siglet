package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableHistogramDataPoints;

public interface ModifiableHistogramDataPoints extends UnmodifiableHistogramDataPoints {

    ModifiableHistogramDataPoint getAt(int i);

    void remove(int i);
}

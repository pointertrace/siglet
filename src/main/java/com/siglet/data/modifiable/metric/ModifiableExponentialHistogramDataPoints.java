package com.siglet.data.modifiable.metric;

import com.siglet.data.unmodifiable.metric.UnmodifiableExponentialHistogramDataPoints;

public interface ModifiableExponentialHistogramDataPoints extends UnmodifiableExponentialHistogramDataPoints {

    ModifiableExponentialHistogramDataPoint getAt(int i);

    void remove(int i);
}

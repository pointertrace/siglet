package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableExponentialHistogramDataPoints {

    int getSize();

    UnmodifiableExponentialHistogramDataPoint getAt(int i);
}

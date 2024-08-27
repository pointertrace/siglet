package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableHistogramDataPoints {

    int getSize();

    UnmodifiableHistogramDataPoint getAt(int i);
}

package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableNumberDataPoints {

    int getSize();

    UnmodifiableNumberDataPoint getAt(int i);
}

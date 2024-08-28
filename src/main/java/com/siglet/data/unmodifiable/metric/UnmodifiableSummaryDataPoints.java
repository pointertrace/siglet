package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableSummaryDataPoints {

    int getSize();

    UnmodifiableSummaryDataPoint getAt(int i);
}

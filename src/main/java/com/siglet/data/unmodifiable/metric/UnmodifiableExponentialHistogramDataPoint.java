package com.siglet.data.unmodifiable.metric;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

public interface UnmodifiableExponentialHistogramDataPoint {

    UnmodifiableAttributes getAttributes();

    UnmodifiableExemplars getExemplars();

    long getStartTimeUnixNano();

    long getTimeUnixNano();

    long getCount();

    int getFlags();

    double getSum();

    int getScale();

    long getZeroCount();

    double getMax();

    double getMin();

    double getZeoThreshold();

    UnmodifiableBuckets getPositive();

    UnmodifiableBuckets getNegative();

}

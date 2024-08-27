package com.siglet.data.unmodifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.modifiable.metric.ModifiableExemplars;
import com.siglet.data.unmodifiable.UnmodifiableAttributes;
import io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint;

import java.util.List;

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

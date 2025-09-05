package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

public interface ExponentialHistogramDataPoint {

    Attributes getAttributes();

    Exemplars getExemplars();

    long getStartTimeUnixNano();

    ExponentialHistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    long getTimeUnixNano();

    ExponentialHistogramDataPoint setTimeUnixNano(long timeUnixNano);

    long getCount();

    ExponentialHistogramDataPoint setCount(long count);

    int getFlags();

    ExponentialHistogramDataPoint setFlags(int flags);

    double getSum();

    ExponentialHistogramDataPoint setSum(double sum);

    int getScale();

    ExponentialHistogramDataPoint setScale(int scale);

    long getZeroCount();

    ExponentialHistogramDataPoint setZeroCount(long zeroCount);

    double getMax();

    ExponentialHistogramDataPoint setMax(double max);

    double getMin();

    ExponentialHistogramDataPoint setMin(double min);

    double getZeoThreshold();

    ExponentialHistogramDataPoint setZeroThreshold(double zeroThreshold);

    Buckets getPositive();

    Buckets getNegative();

}

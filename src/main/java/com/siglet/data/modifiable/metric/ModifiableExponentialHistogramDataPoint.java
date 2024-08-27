package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableExponentialHistogramDataPoint;

public interface ModifiableExponentialHistogramDataPoint extends UnmodifiableExponentialHistogramDataPoint {

    ModifiableAttributes getAttributes();

    ModifiableExemplars getExemplars();

    ModifiableExponentialHistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    ModifiableExponentialHistogramDataPoint setTimeUnixNano(long timeUnixNano);

    ModifiableExponentialHistogramDataPoint setCount(long count);

    ModifiableExponentialHistogramDataPoint setFlags(int flags);

    ModifiableExponentialHistogramDataPoint setSum(double sum);

    ModifiableExponentialHistogramDataPoint setScale(int scale);

    ModifiableExponentialHistogramDataPoint setZeroCount(long zeroCount);

    ModifiableExponentialHistogramDataPoint setMax(double max);

    ModifiableExponentialHistogramDataPoint setMin(double min);

    ModifiableExponentialHistogramDataPoint setZeroThreshold(double zeroThreshold);

    ModifiableBuckets getPositive();

    ModifiableBuckets getNegative();

}

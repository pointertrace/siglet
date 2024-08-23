package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableHistogramDataPoint;

import java.util.List;

public interface ModifiableHistogramDataPoint extends UnmodifiableHistogramDataPoint {


    ModifiableAttributes getAttributes();

    ModifiableExemplars getExemplars();

    ModifiableHistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    ModifiableHistogramDataPoint setTimeUnixNano(long timeUnixNano);

    ModifiableHistogramDataPoint setCount(long count);

    ModifiableHistogramDataPoint setFlags(int flags);

    ModifiableHistogramDataPoint setSum(double sum);

    ModifiableHistogramDataPoint addBucketCount(long count);

    ModifiableHistogramDataPoint addAllBucketCounts(List<Long> count);

    ModifiableHistogramDataPoint clearBucketCounts();

    ModifiableHistogramDataPoint addExplicitBound(Double explicitBound);

    ModifiableHistogramDataPoint addAllExplicitBounds(List<Double> count);

    ModifiableHistogramDataPoint clearExplicitBounds();

    ModifiableHistogramDataPoint  setMin(Double min);

    ModifiableHistogramDataPoint setMax(Double max);

}

package com.siglet.data.unmodifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.modifiable.metric.ModifiableExemplars;

import java.util.List;

public interface UnmodifiableHistogramDataPoint {

    ModifiableAttributes getAttributes();

    ModifiableExemplars getExemplars();

    long getStartTimeUnixNano();

    long getTimeUnixNano();

    long getCount();

    int getFlags();

    double getSum();

    List<Long> getBucketCounts();

    List<Double> getExplicitBounds();

    Double getMin();

    Double getMax();

}

package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableSum extends UnmodifiableData {

    UnmodifiableNumberDataPoints getDataPoints();

    boolean isMonotonic();

    AggregationTemporality getAggregationTemporality();

}

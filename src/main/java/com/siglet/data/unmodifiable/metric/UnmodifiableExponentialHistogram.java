package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableExponentialHistogram {

    UnmodifiableNumberDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

}

package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableExponentialHistogram {

    UnmodifiableExponentialHistogramDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

}

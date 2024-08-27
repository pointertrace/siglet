package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableHistogram {

    UnmodifiableHistogramDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

}

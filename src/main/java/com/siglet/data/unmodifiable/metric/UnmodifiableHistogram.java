package com.siglet.data.unmodifiable.metric;

public interface UnmodifiableHistogram {

    UnmodifiableNumberDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

}

package io.github.pointertrace.siglet.api.signal.metric;

public interface Sum extends Data {

    NumberDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

    Sum setAggregationTemporality(AggregationTemporality aggregationTemporality);

    boolean getMonotonic();

    Sum setMonotonic(boolean monotonic);

}

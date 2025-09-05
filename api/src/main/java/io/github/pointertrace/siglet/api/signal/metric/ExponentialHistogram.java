package io.github.pointertrace.siglet.api.signal.metric;

public interface ExponentialHistogram extends Data {

    AggregationTemporality getAggregationTemporality();

    ExponentialHistogramDataPoints getDataPoints();

    ExponentialHistogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}

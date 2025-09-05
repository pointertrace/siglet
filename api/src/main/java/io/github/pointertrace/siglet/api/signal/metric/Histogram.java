package io.github.pointertrace.siglet.api.signal.metric;

public interface Histogram extends Data {


    HistogramDataPoints getDataPoints();

    AggregationTemporality getAggregationTemporality();

    Histogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}

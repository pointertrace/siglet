package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents an exponential histogram metric data type capturing distribution of measurements using exponential bucketing.
 */
public interface ExponentialHistogram extends Data {

    /**
     * Returns the aggregation temporality of this exponential histogram metric.
     *
     * @return aggregation temporality.
     */
    AggregationTemporality getAggregationTemporality();

    /**
     * Returns the collection of data points associated with this exponential histogram metric.
     *
     * @return collection of {@link ExponentialHistogramDataPoint} elements.
     */
    ExponentialHistogramDataPoints getDataPoints();

    /**
     * Sets the aggregation temporality of this exponential histogram metric.
     *
     * @param aggregationTemporality aggregation temporality.
     * @return current instance of {@code ExponentialHistogram}.
     */
    ExponentialHistogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}

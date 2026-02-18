package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a histogram metric data type capturing distribution of measurements.
 */
public interface Histogram extends Data {

    /**
     * Returns the collection of data points associated with this histogram metric.
     *
     * @return collection of {@link HistogramDataPoint} elements.
     */
    HistogramDataPoints getDataPoints();

    /**
     * Returns the aggregation temporality of this histogram metric.
     *
     * @return aggregation temporality.
     */
    AggregationTemporality getAggregationTemporality();

    /**
     * Sets the aggregation temporality of this histogram metric.
     *
     * @param aggregationTemporality aggregation temporality.
     * @return current instance of {@code Histogram}.
     */
    Histogram setAggregationTemporality(AggregationTemporality aggregationTemporality);

}

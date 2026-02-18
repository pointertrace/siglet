package io.github.pointertrace.siglet.api.signal.metric;

/**
 * Represents a sum metric data type capturing cumulative or delta numeric values.
 */
public interface Sum extends Data {

    /**
     * Returns the collection of data points associated with this sum metric.
     *
     * @return collection of {@link NumberDataPoint} elements.
     */
    NumberDataPoints getDataPoints();

    /**
     * Returns the aggregation temporality of this sum metric.
     *
     * @return aggregation temporality.
     */
    AggregationTemporality getAggregationTemporality();

    /**
     * Sets the aggregation temporality of this sum metric.
     *
     * @param aggregationTemporality aggregation temporality.
     * @return current instance of {@code Sum}.
     */
    Sum setAggregationTemporality(AggregationTemporality aggregationTemporality);

    /**
     * Returns whether this sum metric is monotonic.
     *
     * @return {@code true} if the sum is monotonic, {@code false} otherwise.
     */
    boolean getMonotonic();

    /**
     * Sets whether this sum metric is monotonic.
     *
     * @param monotonic {@code true} if the sum is monotonic, {@code false} otherwise.
     * @return current instance of {@code Sum}.
     */
    Sum setMonotonic(boolean monotonic);

}

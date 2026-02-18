package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

import java.util.List;

/**
 * Represents a single histogram data point capturing distribution of measurements.
 */
public interface HistogramDataPoint {

    /**
     * Returns the attribute set describing this data point.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

    /**
     * Returns the exemplars associated with this data point.
     *
     * @return collection of exemplars.
     */
    Exemplars getExemplars();

    /**
     * Returns the start time in Unix nanoseconds.
     *
     * @return start time in Unix nanoseconds.
     */
    long getStartTimeUnixNano();

    /**
     * Sets the start time in Unix nanoseconds.
     *
     * @param startTimeUnixNano start time in Unix nanoseconds.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

    /**
     * Returns the end time in Unix nanoseconds.
     *
     * @return end time in Unix nanoseconds.
     */
    long getTimeUnixNano();

    /**
     * Sets the end time in Unix nanoseconds.
     *
     * @param timeUnixNano end time in Unix nanoseconds.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setTimeUnixNano(long timeUnixNano);

    /**
     * Returns the number of measurements aggregated into this data point.
     *
     * @return count of measurements.
     */
    long getCount();

    /**
     * Sets the number of measurements aggregated into this data point.
     *
     * @param count count of measurements.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setCount(long count);

    /**
     * Returns the flags associated with this data point.
     *
     * @return flags value.
     */
    int getFlags();

    /**
     * Sets the flags associated with this data point.
     *
     * @param flags flags value.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setFlags(int flags);

    /**
     * Returns the sum of all measurements represented by this data point.
     *
     * @return sum of measurements.
     */
    double getSum();

    /**
     * Sets the sum of all measurements represented by this data point.
     *
     * @param sum sum of measurements.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setSum(double sum);

    /**
     * Returns the list of bucket counts for this histogram.
     *
     * @return list of bucket counts.
     */
    List<Long> getBucketCounts();

    /**
     * Adds a bucket count to the histogram.
     *
     * @param count bucket count to add.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint addBucketCount(long count);

    /**
     * Returns the list of explicit bounds for histogram buckets.
     *
     * @return list of explicit bounds.
     */
    List<Double> getExplicitBounds();

    /**
     * Adds all bucket counts from the provided list.
     *
     * @param count list of bucket counts to add.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint addAllBucketCounts(List<Long> count);

    /**
     * Clears all bucket counts from this histogram.
     *
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint clearBucketCounts();

    /**
     * Adds an explicit bound to the histogram.
     *
     * @param explicitBound explicit bound to add.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint addExplicitBound(Double explicitBound);

    /**
     * Adds all explicit bounds from the provided list.
     *
     * @param count list of explicit bounds to add.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint addAllExplicitBounds(List<Double> count);

    /**
     * Clears all explicit bounds from this histogram.
     *
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint clearExplicitBounds();

    /**
     * Returns the minimum value recorded in this histogram.
     *
     * @return minimum value, or {@code null} if not set.
     */
    Double getMin();

    /**
     * Sets the minimum value recorded in this histogram.
     *
     * @param min minimum value.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setMin(Double min);

    /**
     * Returns the maximum value recorded in this histogram.
     *
     * @return maximum value, or {@code null} if not set.
     */
    Double getMax();

    /**
     * Sets the maximum value recorded in this histogram.
     *
     * @param max maximum value.
     * @return current instance of {@code HistogramDataPoint}.
     */
    HistogramDataPoint setMax(Double max);

}

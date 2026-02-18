package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

/**
 * Represents a single exponential histogram data point capturing distribution of measurements using exponential bucketing.
 */
public interface ExponentialHistogramDataPoint {

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
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setStartTimeUnixNano(long startTimeUnixNano);

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
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setTimeUnixNano(long timeUnixNano);

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
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setCount(long count);

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
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setFlags(int flags);

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
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setSum(double sum);

    /**
     * Returns the scale factor used for exponential bucketing.
     *
     * @return scale factor.
     */
    int getScale();

    /**
     * Sets the scale factor used for exponential bucketing.
     *
     * @param scale scale factor.
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setScale(int scale);

    /**
     * Returns the count of measurements that fall within the zero bucket.
     *
     * @return zero bucket count.
     */
    long getZeroCount();

    /**
     * Sets the count of measurements that fall within the zero bucket.
     *
     * @param zeroCount zero bucket count.
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setZeroCount(long zeroCount);

    /**
     * Returns the maximum value recorded in this histogram.
     *
     * @return maximum value.
     */
    double getMax();

    /**
     * Sets the maximum value recorded in this histogram.
     *
     * @param max maximum value.
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setMax(double max);

    /**
     * Returns the minimum value recorded in this histogram.
     *
     * @return minimum value.
     */
    double getMin();

    /**
     * Sets the minimum value recorded in this histogram.
     *
     * @param min minimum value.
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setMin(double min);

    /**
     * Returns the zero threshold value.
     *
     * @return zero threshold.
     */
    double getZeroThreshold();

    /**
     * Sets the zero threshold value.
     *
     * @param zeroThreshold zero threshold.
     * @return current instance of {@code ExponentialHistogramDataPoint}.
     */
    ExponentialHistogramDataPoint setZeroThreshold(double zeroThreshold);

    /**
     * Returns the positive buckets of this exponential histogram.
     *
     * @return positive buckets.
     */
    Buckets getPositive();

    /**
     * Returns the negative buckets of this exponential histogram.
     *
     * @return negative buckets.
     */
    Buckets getNegative();

}

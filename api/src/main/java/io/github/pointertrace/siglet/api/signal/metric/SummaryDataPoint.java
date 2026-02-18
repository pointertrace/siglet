package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

/**
 * Represents a single summary data point capturing aggregated metric statistics.
 */
public interface SummaryDataPoint {

    /**
     * Returns the attribute set describing this data point.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

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
     * @return current instance of {@code SummaryDataPoint}.
     */
    SummaryDataPoint setStartTimeUnixNano(long startTimeUnixNano);

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
     * @return current instance of {@code SummaryDataPoint}.
     */
    SummaryDataPoint setTimeUnixNano(long timeUnixNano);

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
     * @return current instance of {@code SummaryDataPoint}.
     */
    SummaryDataPoint setCount(long count);

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
     * @return current instance of {@code SummaryDataPoint}.
     */
    SummaryDataPoint setFlags(int flags);

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
     * @return current instance of {@code SummaryDataPoint}.
     */
    SummaryDataPoint setSum(double sum);

    /**
     * Returns the quantile values associated with this data point.
     *
     * @return collection of quantile values.
     */
    ValueAtQuantiles getQuantileValues();

}

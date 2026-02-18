package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

/**
 * Represents a single numeric data point capturing a measurement value.
 */
public interface NumberDataPoint {

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
     * @return current instance of {@code NumberDataPoint}.
     */
    NumberDataPoint setStartTimeUnixNano(long startTimeUnixNano);

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
     * @return current instance of {@code NumberDataPoint}.
     */
    NumberDataPoint setTimeUnixNano(long timeUnixNano);

    /**
     * Returns whether this data point contains a long value.
     *
     * @return {@code true} if the value is a long, {@code false} otherwise.
     */
    boolean hasLongValue();

    /**
     * Returns the value as a long.
     *
     * @return long value.
     */
    long getAsLong();

    /**
     * Sets the value as a long.
     *
     * @param value long value.
     * @return current instance of {@code NumberDataPoint}.
     */
    NumberDataPoint setAsLong(long value);

    /**
     * Returns whether this data point contains a double value.
     *
     * @return {@code true} if the value is a double, {@code false} otherwise.
     */
    boolean hasDoubleValue();

    /**
     * Returns the value as a double.
     *
     * @return double value.
     */
    double getAsDouble();

    /**
     * Sets the value as a double.
     *
     * @param value double value.
     * @return current instance of {@code NumberDataPoint}.
     */
    NumberDataPoint setAsDouble(double value);

    /**
     * Returns the raw value object.
     *
     * @return value object.
     */
    Object getValue();

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
     * @return current instance of {@code NumberDataPoint}.
     */
    NumberDataPoint setFlags(int flags);

}

package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

/**
 * Represents a single exemplar measurement that provides context for a metric data point.
 */
public interface Exemplar {

    /**
     * Returns the attribute set describing this exemplar.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

    /**
     * Returns the time in Unix nanoseconds when this exemplar was recorded.
     *
     * @return time in Unix nanoseconds.
     */
    long getTimeUnixNanos();

    /**
     * Sets the time in Unix nanoseconds when this exemplar was recorded.
     *
     * @param timeUnixNanos time in Unix nanoseconds.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setTimeUnixNanos(long timeUnixNanos);

    /**
     * Returns the exemplar value as a long.
     *
     * @return long value.
     */
    long getAsLong();

    /**
     * Sets the exemplar value as a long.
     *
     * @param value long value.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setAsLong(long value);

    /**
     * Returns the exemplar value as a double.
     *
     * @return double value.
     */
    double getAsDouble();

    /**
     * Sets the exemplar value as a double.
     *
     * @param value double value.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setAsDouble(double value);

    /**
     * Returns the span ID associated with this exemplar.
     *
     * @return span ID.
     */
    long getSpanId();

    /**
     * Sets the span ID associated with this exemplar.
     *
     * @param spanId span ID.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setSpanId(long spanId);

    /**
     * Returns the low 64 bits of the trace ID.
     *
     * @return low 64 bits of trace ID.
     */
    long getTraceIdLow();

    /**
     * Returns the high 64 bits of the trace ID.
     *
     * @return high 64 bits of trace ID.
     */
    long getTraceIdHigh();

    /**
     * Returns the trace ID as a byte array.
     *
     * @return trace ID as byte array.
     */
    byte[] getTraceId();

    /**
     * Sets the trace ID using high and low 64-bit values.
     *
     * @param traceIdHigh high 64 bits of trace ID.
     * @param traceIdLow low 64 bits of trace ID.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setTraceId(long traceIdHigh, long traceIdLow);

    /**
     * Sets the trace ID using a byte array.
     *
     * @param traceId trace ID as byte array.
     * @return current instance of {@code Exemplar}.
     */
    Exemplar setTraceId(byte[] traceId);
}

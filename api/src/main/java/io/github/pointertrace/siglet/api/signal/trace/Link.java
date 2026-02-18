package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.signal.Attributes;

/**
 * Represents a link to another span, establishing a relationship between spans.
 */
public interface Link {

    /**
     * Returns the trace ID as a hexadecimal string.
     *
     * @return trace ID as hexadecimal string.
     */
    String getTraceIdEx();

    /**
     * Returns the high 64 bits of the trace ID.
     *
     * @return high 64 bits of trace ID.
     */
    long getTraceIdHigh();

    /**
     * Returns the low 64 bits of the trace ID.
     *
     * @return low 64 bits of trace ID.
     */
    long getTraceIdLow();

    /**
     * Sets the trace ID using high and low 64-bit values.
     *
     * @param high high 64 bits of trace ID.
     * @param low low 64 bits of trace ID.
     * @return current instance of {@code Link}.
     */
    Link setTraceId(long high, long low);

    /**
     * Returns the span ID as a hexadecimal string.
     *
     * @return span ID as hexadecimal string.
     */
    String getSpanIdEx();

    /**
     * Returns the span ID.
     *
     * @return span ID.
     */
    long getSpanId();

    /**
     * Sets the span ID.
     *
     * @param spanId span ID.
     * @return current instance of {@code Link}.
     */
    Link setSpanId(long spanId);

    /**
     * Returns the trace state of the linked span.
     *
     * @return trace state.
     */
    String getTraceState();

    /**
     * Sets the trace state of the linked span.
     *
     * @param traceState trace state.
     * @return current instance of {@code Link}.
     */
    Link setTraceState(String traceState);

    /**
     * Returns the flags associated with this link.
     *
     * @return flags value.
     */
    int getFlags();

    /**
     * Sets the flags associated with this link.
     *
     * @param flags flags value.
     * @return current instance of {@code Link}.
     */
    Link setFlags(int flags);

    /**
     * Returns the number of dropped attributes.
     *
     * @return number of dropped attributes.
     */
    int getDroppedAttributesCount();

    /**
     * Sets the number of dropped attributes.
     *
     * @param droppedAttributesCount number of dropped attributes.
     * @return current instance of {@code Link}.
     */
    Link setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the attribute set describing this link.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

}

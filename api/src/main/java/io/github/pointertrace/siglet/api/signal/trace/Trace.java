package io.github.pointertrace.siglet.api.signal.trace;

/**
 * Represents a collection of spans that share the same trace ID.
 */
public interface Trace {

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
     * Returns the trace ID as a byte array.
     *
     * @return trace ID as byte array.
     */
    byte[] getTraceId();

    /**
     * Returns the trace ID as a hexadecimal string.
     *
     * @return trace ID as hexadecimal string.
     */
    String getTraceIdEx();

    /**
     * Returns the number of spans in this trace.
     *
     * @return number of spans.
     */
    int getSize();

    /**
     * Gets a specific span by index.
     *
     * @param index span index in the trace.
     * @return {@link Span}.
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= getSize()}).
     */
    Span getAt(int index);

    /**
     * Checks if the trace is complete (all spans have been received).
     *
     * @return {@code true} if the trace is complete, {@code false} otherwise.
     */
    boolean isComplete();

    /**
     * Adds a span to this trace.
     *
     * @param span span to add.
     * @return current instance of {@code Trace}.
     */
    Trace add(Span span);

    /**
     * Removes a span from this trace by its span ID.
     *
     * @param spanId span ID of the span to remove.
     * @return {@code true} if the span was removed, {@code false} otherwise.
     */
    boolean remove(long spanId);

    /**
     * Gets a span from this trace by its span ID.
     *
     * @param spanId span ID of the span to retrieve.
     * @return {@link Span} if found, {@code null} otherwise.
     */
    Span get(long spanId);

}

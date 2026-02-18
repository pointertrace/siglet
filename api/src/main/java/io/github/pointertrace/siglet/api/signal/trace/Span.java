package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.Events;
import io.github.pointertrace.siglet.api.signal.InstrumentationScope;
import io.github.pointertrace.siglet.api.signal.Resource;

/**
 * Represents a span signal capturing a unit of work in a distributed trace.
 */
public interface Span extends Signal {

    /**
     * Returns whether this span is a root span (has no parent).
     *
     * @return {@code true} if this is a root span, {@code false} otherwise.
     */
    boolean isRoot();

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
     * Returns the trace ID as a byte array.
     *
     * @return trace ID as byte array.
     */
    byte[] getTraceId();

    /**
     * Sets the trace ID using high and low 64-bit values.
     *
     * @param high high 64 bits of trace ID.
     * @param low low 64 bits of trace ID.
     * @return current instance of {@code Span}.
     */
    Span setTraceId(long high, long low);

    /**
     * Sets the trace ID using a byte array.
     *
     * @param traceId trace ID as byte array.
     * @return current instance of {@code Span}.
     */
    Span setTraceId(byte[] traceId);

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
     * @return current instance of {@code Span}.
     */
    Span setSpanId(long spanId);

    /**
     * Returns the parent span ID as a hexadecimal string.
     *
     * @return parent span ID as hexadecimal string.
     */
    String getParentSpanIdEx();

    /**
     * Returns the parent span ID.
     *
     * @return parent span ID.
     */
    long getParentSpanId();

    /**
     * Sets the parent span ID.
     *
     * @param parentSpanId parent span ID.
     * @return current instance of {@code Span}.
     */
    Span setParentSpanId(long parentSpanId);

    /**
     * Returns the trace state of this span.
     *
     * @return trace state.
     */
    String getTraceState();

    /**
     * Sets the trace state of this span.
     *
     * @param traceState trace state.
     * @return current instance of {@code Span}.
     */
    Span setTraceState(String traceState);

    /**
     * Returns the name of this span.
     *
     * @return span name.
     */
    String getName();

    /**
     * Sets the name of this span.
     *
     * @param name span name.
     * @return current instance of {@code Span}.
     */
    Span setName(String name);

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
     * @return current instance of {@code Span}.
     */
    Span setStartTimeUnixNano(long startTimeUnixNano);

    /**
     * Returns the end time in Unix nanoseconds.
     *
     * @return end time in Unix nanoseconds.
     */
    long getEndTimeUnixNano();

    /**
     * Sets the end time in Unix nanoseconds.
     *
     * @param endTimeUnixNano end time in Unix nanoseconds.
     * @return current instance of {@code Span}.
     */
    Span setEndTimeUnixNano(long endTimeUnixNano);

    /**
     * Returns the kind of this span.
     *
     * @return span kind.
     */
    SpanKind getKind();

    /**
     * Sets the kind of this span.
     *
     * @param kind span kind.
     * @return current instance of {@code Span}.
     */
    Span setKind(SpanKind kind);

    /**
     * Returns the status of this span.
     *
     * @return span status.
     */
    Status getStatus();

    /**
     * Returns the attribute set describing this span.
     *
     * @return attribute set.
     */
    Attributes getAttributes();

    /**
     * Returns the links associated with this span.
     *
     * @return collection of links.
     */
    Links getLinks();

    /**
     * Returns the events associated with this span.
     *
     * @return collection of events.
     */
    Events getEvents();

    /**
     * Returns the resource associated with this span.
     *
     * @return resource.
     */
    Resource getResource();

    /**
     * Returns the instrumentation scope associated with this span.
     *
     * @return instrumentation scope.
     */
    InstrumentationScope getInstrumentationScope();

    /**
     * Returns the flags associated with this span.
     *
     * @return flags value.
     */
    int getFlags();

    /**
     * Sets the flags associated with this span.
     *
     * @param flags flags value.
     * @return current instance of {@code Span}.
     */
    Span setFlags(int flags);

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
     * @return current instance of {@code Span}.
     */
    Span setDroppedAttributesCount(int droppedAttributesCount);

    /**
     * Returns the number of dropped events.
     *
     * @return number of dropped events.
     */
    int getDroppedEventsCount();

    /**
     * Sets the number of dropped events.
     *
     * @param droppedEventsCount number of dropped events.
     * @return current instance of {@code Span}.
     */
    Span setDroppedEventsCount(int droppedEventsCount);

    /**
     * Returns the number of dropped links.
     *
     * @return number of dropped links.
     */
    int getDroppedLinksCount();

    /**
     * Sets the number of dropped links.
     *
     * @param droppedLinksCount number of dropped links.
     * @return current instance of {@code Span}.
     */
    Span setDroppedLinksCount(int droppedLinksCount);
}

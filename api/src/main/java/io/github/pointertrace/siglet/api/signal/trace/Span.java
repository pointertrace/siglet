package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.Attributes;
import io.github.pointertrace.siglet.api.signal.Events;
import io.github.pointertrace.siglet.api.signal.InstrumentationScope;
import io.github.pointertrace.siglet.api.signal.Resource;

public interface Span extends Signal {

    boolean isRoot();

    String getTraceIdEx();

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();

    Span setTraceId(long high, long low);

    Span setTraceId(byte[] traceId);

    String getSpanIdEx();

    long getSpanId();

    Span setSpanId(long spanId);

    String getParentSpanIdEx();

    long getParentSpanId();

    Span setParentSpanId(long parentSpanId);

    String getTraceState();

    Span setTraceState(String traceState);

    String getName();

    Span setName(String name);

    long getStartTimeUnixNano();

    Span setStartTimeUnixNano(long startTimeUnixNano);

    long getEndTimeUnixNano();

    Span setEndTimeUnixNano(long endTimeUnixNano);

    SpanKind getKind();

    Span setKind(SpanKind kind);

    Status getStatus();

    Attributes getAttributes();

    Links getLinks();

    Events getEvents();

    Resource getResource();

    InstrumentationScope getInstrumentationScope();

    int getFlags() ;

    Span setFlags(int flags);

    int getDroppedAttributesCount() ;

    Span setDroppedAttributesCount(int droppedAttributesCount);

    int getDroppedEventsCount() ;

    Span setDroppedEventsCount(int droppedEventsCount);

    int getDroppedLinksCount() ;

    Span setDroppedLinksCount(int droppedLinksCount);
}

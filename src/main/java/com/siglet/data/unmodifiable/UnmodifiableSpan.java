package com.siglet.data.unmodifiable;

import com.siglet.data.trace.SpanKind;

public interface UnmodifiableSpan {

    long getTraceIdHigh();

    long getTraceIdLow();

    long getSpanId();

    long getParentSpanId();

    String getTraceState();

    String getName();

    long getStartUnixNano();

    long getEndUnixNano();

    SpanKind getKind();

    UnmodifiableAttributes getAttributes();

    UnmodifiableResource getResource();

    UnmodifiableInstrumentationScope getInstrumentationScope();

    int getFlags() ;

    int getDroppedAttributesCount() ;

    int getDroppedEventsCount() ;

    int getDroppedLinksCount() ;

}

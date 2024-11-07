package com.siglet.data.unmodifiable.trace;

import com.siglet.data.trace.SpanKind;
import com.siglet.data.unmodifiable.UnmodifiableAttributes;
import com.siglet.data.unmodifiable.UnmodifiableInstrumentationScope;
import com.siglet.data.unmodifiable.UnmodifiableResource;

public interface UnmodifiableSpan {

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();

    long getSpanId();

    long getParentSpanId();

    String getTraceState();

    String getName();

    long getStartTimeUnixNano();

    long getEndTimeUnixNano();

    SpanKind getKind();

    UnmodifiableStatus getStatus();

    UnmodifiableAttributes getAttributes();

    UnmodifiableResource getResource();

    UnmodifiableInstrumentationScope getInstrumentationScope();

    int getFlags() ;

    int getDroppedAttributesCount() ;

    int getDroppedEventsCount() ;

    int getDroppedLinksCount() ;

}

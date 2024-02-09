package com.siglet.data.trace;

import com.siglet.data.common.UnmodifiedAttributes;

public interface UnmodifiableSpan {

    String getTraceId();

    String getSpanId();

    String getParentSpanId();

    String getTraceState();

    String getName();

    long getStartUnixNano();

    long getEndUnixNano();

    SpanKind getKind();

    UnmodifiableResource getResource();

    UnmodifiableInstrumentationScope getInstrumentationScope();


}

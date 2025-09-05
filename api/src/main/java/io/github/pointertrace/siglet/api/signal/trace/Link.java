package io.github.pointertrace.siglet.api.signal.trace;

import io.github.pointertrace.siglet.api.signal.Attributes;

public interface Link {

    String getTraceIdEx();

    long getTraceIdHigh();

    long getTraceIdLow();

    Link setTraceId(long high, long low);

    String getSpanIdEx();

    long getSpanId();

    Link setSpanId(long spanId);

    String getTraceState() ;

    Link setTraceState(String traceState);

    int getFlags() ;

    Link setFlags(int flags);

    int getDroppedAttributesCount() ;

    Link setDroppedAttributesCount(int droppedAttributesCount);

    Attributes getAttributes();

}

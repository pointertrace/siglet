package com.siglet.data.unmodifiable;

public interface UnmodifiableLink {

    long getTraceIdHigh();

    long getTraceIdLow();

    long getSpanId();

    void setSpanId(long spanId) ;

    String getTraceState() ;

    int getFlags() ;

    int getDroppedAttributesCount() ;

    UnmodifiableAttributes getAttributes() ;

}

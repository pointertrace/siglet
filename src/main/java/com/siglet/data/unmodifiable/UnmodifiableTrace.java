package com.siglet.data.unmodifiable;

public interface UnmodifiableTrace {

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();

    UnmodifiableSpan get(long spanId);

    boolean isComplete();

    int getSize();
}

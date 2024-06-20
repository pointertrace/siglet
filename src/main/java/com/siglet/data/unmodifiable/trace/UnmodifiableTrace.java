package com.siglet.data.unmodifiable.trace;

public interface UnmodifiableTrace {

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();

    UnmodifiableSpan get(long spanId);

    UnmodifiableSpan getAt(int index);

    boolean isComplete();

    int getSize();
}

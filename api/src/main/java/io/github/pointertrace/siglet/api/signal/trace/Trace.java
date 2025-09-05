package io.github.pointertrace.siglet.api.signal.trace;


public interface Trace {

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();

    String getTraceIdEx();

    int getSize();

    Span getAt(int index);

    boolean isComplete();

    Trace add(Span span);

    boolean remove(long spanId);

    Span get(long spanId);

}

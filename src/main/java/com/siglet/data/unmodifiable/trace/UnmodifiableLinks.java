package com.siglet.data.unmodifiable.trace;

public interface UnmodifiableLinks {

    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    UnmodifiableLink get(long traceIdHigh, long traceIdLow, int spanId);

    int getSize();

}

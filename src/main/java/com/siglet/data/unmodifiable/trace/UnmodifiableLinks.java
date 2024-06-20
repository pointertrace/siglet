package com.siglet.data.unmodifiable.trace;

import com.siglet.data.unmodifiable.trace.UnmodifiableLink;

import java.util.Map;

public interface UnmodifiableLinks {

    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    UnmodifiableLink get(long traceIdHigh, long traceIdLow, int spanId);

    void add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes);

    boolean remove(long traceIdHigh, long traceIdLow, int spanId);

    int size();

}

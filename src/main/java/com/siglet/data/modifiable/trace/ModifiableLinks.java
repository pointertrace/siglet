package com.siglet.data.modifiable.trace;

import com.siglet.data.unmodifiable.trace.UnmodifiableLinks;

import java.util.Map;

public interface ModifiableLinks extends UnmodifiableLinks {

    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    ModifiableLink add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes);

    ModifiableLink get(long traceIdHigh, long traceIdLow, int spanId);

    boolean remove(long traceIdHigh, long traceIdLow, int spanId);

}

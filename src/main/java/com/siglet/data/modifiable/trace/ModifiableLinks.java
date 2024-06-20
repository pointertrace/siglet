package com.siglet.data.modifiable.trace;

import com.siglet.data.unmodifiable.trace.UnmodifiableLinks;

import java.util.Map;
import java.util.stream.Stream;

public interface ModifiableLinks extends UnmodifiableLinks {

    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    void add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes);

    boolean remove(long traceIdHigh, long traceIdLow, int spanId);

    Stream<? extends ModifiableLink> stream();
}

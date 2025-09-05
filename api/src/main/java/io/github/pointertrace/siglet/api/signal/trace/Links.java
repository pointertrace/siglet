package io.github.pointertrace.siglet.api.signal.trace;

import java.util.Map;

public interface Links {

    int getSize();

    boolean has(long traceIdHigh, long traceIdLow, long spanId);

    Link add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes);

    Link get(long traceIdHigh, long traceIdLow, int spanId);

    boolean remove(long traceIdHigh, long traceIdLow, int spanId);

}

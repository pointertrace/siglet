package com.siglet.data.trace;

import io.opencensus.trace.Tracestate;

public interface ModifiableSpan extends UnmodifiableSpan{


    void setTraceState(String traceState);

    void setName(String name);

    void setStartUnixNano(long startUnixNano);

    void setEndUnixNano(long endUnixNano);

    void setKind(SpanKind spanKind);

    ModifiableResource getResource();
}

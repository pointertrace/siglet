package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableExemplar;

public interface ModifiableExemplar extends UnmodifiableExemplar {

    ModifiableAttributes getAttributes();

    void setTimeUnixNanos(long timeUnixNanos);

    void setAsLong(long value);

    void setAsDouble(double value);

    void setSpanId(long spanId);

    void setTraceId(long traceIdHigh, long traceIdLow);

    void setTraceId(byte[] traceId);
}

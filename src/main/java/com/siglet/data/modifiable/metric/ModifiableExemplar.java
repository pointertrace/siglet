package com.siglet.data.modifiable.metric;

import com.siglet.data.modifiable.ModifiableAttributes;
import com.siglet.data.unmodifiable.metric.UnmodifiableExemplar;

public interface ModifiableExemplar extends UnmodifiableExemplar {

    ModifiableAttributes getAttributes();

    ModifiableExemplar setTimeUnixNanos(long timeUnixNanos);

    ModifiableExemplar setAsLong(long value);

    ModifiableExemplar setAsDouble(double value);

    ModifiableExemplar setSpanId(long spanId);

    ModifiableExemplar setTraceId(long traceIdHigh, long traceIdLow);

    ModifiableExemplar setTraceId(byte[] traceId);
}

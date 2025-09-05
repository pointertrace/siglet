package io.github.pointertrace.siglet.api.signal.metric;

import io.github.pointertrace.siglet.api.signal.Attributes;

public interface Exemplar {

    Attributes getAttributes();

    long getTimeUnixNanos();

    Exemplar setTimeUnixNanos(long timeUnixNanos);

    long getAsLong();

    Exemplar setAsLong(long value);

    double getAsDouble();

    Exemplar setAsDouble(double value);

    long getSpanId();

    Exemplar setSpanId(long spanId);

    long getTraceIdLow();

    long getTraceIdHigh();

    byte[] getTraceId();

    Exemplar setTraceId(long traceIdHigh, long traceIdLow);

    Exemplar setTraceId(byte[] traceId);
}

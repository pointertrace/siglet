package com.siglet.data.unmodifiable.metric;

import com.siglet.data.unmodifiable.UnmodifiableAttributes;

public interface UnmodifiableExemplar {

    UnmodifiableAttributes getAttributes();

    long getTimeUnixNanos();

    long getAsLong();

    double getAsDouble();

    long getSpanId();

    long getTraceIdHigh();

    long getTraceIdLow();

    byte[] getTraceId();


}

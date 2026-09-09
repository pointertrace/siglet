package io.github.pointertrace.siglet.impl.engine.metric.otelgrpc;

import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;

import java.util.concurrent.atomic.AtomicLong;

public class OtelGrpcLongGauge implements LongGauge {

    private final AtomicLong gauge;


    public OtelGrpcLongGauge(AtomicLong gauge) {
        this.gauge = gauge;
    }

    public void set(long value) {
        gauge.set(value);
    }
}

package io.github.pointertrace.siglet.impl.engine.metric.otelgrpc;


import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.LongAdder;

public class OtelGrpcLongCounter implements LongCounter {

    private final LongAdder counter;


    public OtelGrpcLongCounter(LongAdder counter) {
        this.counter = counter;
    }

    public void increment(long amount) {
        counter.add(amount);
    }

    public void increment() {
        counter.increment();
    }
}

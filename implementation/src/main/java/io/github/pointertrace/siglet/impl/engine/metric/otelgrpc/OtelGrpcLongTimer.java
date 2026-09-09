package io.github.pointertrace.siglet.impl.engine.metric.otelgrpc;

import io.github.pointertrace.siglet.impl.engine.metric.LongTimer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;

import java.util.concurrent.TimeUnit;

public class OtelGrpcLongTimer implements LongTimer {


    private final LongHistogram histogram;
    private final Attributes attributes;

    public OtelGrpcLongTimer(LongHistogram histogram, Attributes attributes) {
        this.histogram = histogram;
        this.attributes = Attributes.builder().putAll(attributes).build();
    }

    public void record(long duration, TimeUnit unit) {
        histogram.record(unit.toMillis(duration), attributes);
    }

}

package io.github.pointertrace.siglet.impl.eventloop.processor;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.resource.v1.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ContextImpl<T> implements Context<T> {

    private final T config;

    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public ContextImpl(T config) {
        this.config = config;
    }

    public ContextImpl() {
        this(null);
    }

    @Override
    public ConcurrentMap<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public io.github.pointertrace.siglet.api.signal.metric.Metric newGauge() {
        return newGauge(null);
    }

    @Override
    public MetricAdapter newGauge(Signal baseSignal) {

        io.opentelemetry.proto.metrics.v1.Gauge gauge = io.opentelemetry.proto.metrics.v1.Gauge.newBuilder()
                .build();

        Metric metric =  Metric.newBuilder()
                .setGauge(gauge)
                .build();

        Resource resource = getResource(baseSignal);
        InstrumentationScope instrumentationScope = getInstrumentationScope(baseSignal);

        return new MetricAdapter(metric,resource,instrumentationScope);

    }

    @Override
    public io.github.pointertrace.siglet.api.signal.metric.Metric newSum() {
        return null;
    }

    @Override
    public MetricAdapter newSum(Signal baseSignal) {

        io.opentelemetry.proto.metrics.v1.Sum sum = io.opentelemetry.proto.metrics.v1.Sum.newBuilder()
                .build();

        Metric metric =  Metric.newBuilder()
                .setSum(sum)
                .build();

        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        return new MetricAdapter(metric,resource,instrumentationScope);
    }

    private Resource getResource(Signal signal) {
        if (signal instanceof SpanAdapter span) {
            return span.getUpdatedResource().toBuilder().build();
        } else if (signal instanceof MetricAdapter metric) {
            return metric.getUpdatedResource().toBuilder().build();
        } else {
            return Resource.newBuilder().build();
        }
    }


    private InstrumentationScope getInstrumentationScope(Signal signal) {
        if (signal instanceof SpanAdapter span) {
            return span.getUpdatedScope().toBuilder().build();
        } else if (signal instanceof MetricAdapter metric) {
            return metric.getUpdatedScope().toBuilder().build();
        } else {
            return InstrumentationScope.newBuilder().build();
        }
    }

}

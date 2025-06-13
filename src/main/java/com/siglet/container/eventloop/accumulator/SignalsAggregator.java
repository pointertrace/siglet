package com.siglet.container.eventloop.accumulator;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SignalsAggregator {


    private final ExportTraceServiceRequest.Builder exportTraceServiceRequestBuilder =
            ExportTraceServiceRequest.newBuilder();

    private final ExportMetricsServiceRequest.Builder exportMetricsServiceRequestBuilder =
            ExportMetricsServiceRequest.newBuilder();

    public void add(Span span, InstrumentationScope scope, Resource resource) {

        ResourceSpans.Builder resourceSpanBuilder = findOrDefault(
                exportTraceServiceRequestBuilder.getResourceSpansBuilderList(),
                rsSpanBld -> resource.equals(rsSpanBld.getResource()),
                ResourceSpans::newBuilder,
                exportTraceServiceRequestBuilder::addResourceSpans
        );

        resourceSpanBuilder.setResource(resource);

        ScopeSpans.Builder scopeSpanBuilder = findOrDefault(
                resourceSpanBuilder.getScopeSpansBuilderList(),
                scSpanBld -> scope.equals(scSpanBld.getScope()),
                ScopeSpans::newBuilder,
                resourceSpanBuilder::addScopeSpans
        );

        scopeSpanBuilder.setScope(scope);

        scopeSpanBuilder.addSpans(span);

    }

    public void add(Metric metric, InstrumentationScope scope, Resource resource) {

        ResourceMetrics.Builder resourceMetricBuilder = findOrDefault(
                exportMetricsServiceRequestBuilder.getResourceMetricsBuilderList(),
                rsMetricBld -> resource.equals(rsMetricBld.getResource()),
                ResourceMetrics::newBuilder,
                exportMetricsServiceRequestBuilder::addResourceMetrics
        );

        resourceMetricBuilder.setResource(resource);

        ScopeMetrics.Builder scopeMetricBuilder = findOrDefault(
                resourceMetricBuilder.getScopeMetricsBuilderList(),
                scMetricBld -> scope.equals(scMetricBld.getScope()),
                ScopeMetrics::newBuilder,
                resourceMetricBuilder::addScopeMetrics
        );

        scopeMetricBuilder.setScope(scope);

        scopeMetricBuilder.addMetrics(metric);

    }

    public ExportMetricsServiceRequest.Builder getExportMetricsServiceRequestBuilder() {
        return exportMetricsServiceRequestBuilder;
    }

    public ExportTraceServiceRequest.Builder getExportTraceServiceRequestBuilder() {
        return exportTraceServiceRequestBuilder;
    }

    private <T> T findOrDefault(List<T> list, Predicate<T> predicate, Supplier<T> defaultValue, Consumer<T> listAdder) {
        for (T e : list) {
            if (predicate.test(e)) {
                return e;
            }
        }
        T newValue = defaultValue.get();
        listAdder.accept(newValue);
        return newValue;
    }

}

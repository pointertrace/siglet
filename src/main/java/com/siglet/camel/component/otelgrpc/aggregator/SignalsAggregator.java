package com.siglet.camel.component.otelgrpc.aggregator;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SignalsAggregator {

    private final List<ResourceSpans.Builder> resourceSpansBuilders = new ArrayList<>();

    private final List<ResourceMetrics.Builder> resourceMetricsBuilders = new ArrayList<>();

    public void add(ProtoSpanAdapter spanAdapter) {
        Resource rsToAdd = spanAdapter.getUpdatedResource();
        InstrumentationScope isToAdd = spanAdapter.getUpdatedInstrumentationScope();
        Span spanToAdd = spanAdapter.getUpdated();
        if (!resourceSpansBuilders.isEmpty()) {
            for (ResourceSpans.Builder resourceSpanBld : resourceSpansBuilders) {
                if (rsToAdd.equals(resourceSpanBld.getResource())) {
                    for (ScopeSpans.Builder scopedSpanBld : resourceSpanBld.getScopeSpansBuilderList()) {
                        if (isToAdd.equals(scopedSpanBld.getScope())) {
                            scopedSpanBld.addSpans(spanToAdd);
                            return;
                        }
                    }
                    resourceSpanBld.addScopeSpansBuilder().setScope(isToAdd).addSpans(spanToAdd);
                    return;
                }
            }
            resourceSpansBuilders.add(
                    ResourceSpans.newBuilder()
                            .setResource(rsToAdd)
                            .addScopeSpans(ScopeSpans.newBuilder()
                                    .setScope(isToAdd)
                                    .addSpans(spanToAdd)
                                    .build()));
        } else {
            resourceSpansBuilders.add(ResourceSpans.newBuilder()
                    .setResource(rsToAdd)
                    .addScopeSpans(ScopeSpans.newBuilder()
                            .setScope(isToAdd)
                            .addSpans(spanToAdd)
                            .build()));
        }
    }

    public void add(ProtoMetricAdapter metricAdapter) {
        Resource rsToAdd = metricAdapter.getUpdatedResource();
        InstrumentationScope isToAdd = metricAdapter.getUpdatedInstrumentationScope();
        Metric metricToAdd = metricAdapter.getUpdated();
        if (!resourceMetricsBuilders.isEmpty()) {
            for (ResourceMetrics.Builder resourceMetricBld : resourceMetricsBuilders) {
                if (rsToAdd.equals(resourceMetricBld.getResource())) {
                    for (ScopeMetrics.Builder scopedMetricBld : resourceMetricBld.getScopeMetricsBuilderList()) {
                        if (isToAdd.equals(scopedMetricBld.getScope())) {
                            scopedMetricBld.addMetrics(metricToAdd);
                            return;
                        }
                    }
                    resourceMetricBld.addScopeMetricsBuilder().setScope(isToAdd).addMetrics(metricToAdd);
                    return;
                }
            }
            resourceMetricsBuilders.add(
                    ResourceMetrics.newBuilder()
                            .setResource(rsToAdd)
                            .addScopeMetrics(ScopeMetrics.newBuilder()
                                    .setScope(isToAdd)
                                    .addMetrics(metricToAdd)
                                    .build()));
        } else {
            resourceMetricsBuilders.add(ResourceMetrics.newBuilder()
                    .setResource(rsToAdd)
                    .addScopeMetrics(ScopeMetrics.newBuilder()
                            .setScope(isToAdd)
                            .addMetrics(metricToAdd)
                            .build()));

        }
    }

    public void consumeMetricsBuilder(Consumer<ResourceMetrics.Builder>  metricsBuilderConsumer) {
        resourceMetricsBuilders.forEach(metricsBuilderConsumer);
    }

    public void consumeSpansBuilder(Consumer<ResourceSpans.Builder>  spansBuilderConsumer) {
        resourceSpansBuilders.forEach(spansBuilderConsumer);
    }

    public boolean hasMetrics() {
        return !resourceMetricsBuilders.isEmpty();
    }

    public boolean hasSpans() {
        return !resourceSpansBuilders.isEmpty();
    }


}

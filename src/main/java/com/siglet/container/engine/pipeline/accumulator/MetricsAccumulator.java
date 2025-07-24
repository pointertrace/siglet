package com.siglet.container.engine.pipeline.accumulator;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsAccumulator {


    private final Map<ResourceMetrics.Builder, List<ScopeMetrics.Builder>> scopes = new HashMap<>();

    private final ExportMetricsServiceRequest.Builder exportMetricsServiceRequestBuilder =
            ExportMetricsServiceRequest.newBuilder();

    public void add(Metric metric, InstrumentationScope scope, Resource resource) {

        ResourceMetrics.Builder resourceMetrics = null;
        for (ResourceMetrics.Builder currentResourceMetrics : scopes.keySet()) {
            if (currentResourceMetrics.getResource().equals(resource)) {
                resourceMetrics = currentResourceMetrics;
                break;
            }
        }
        if (resourceMetrics == null) {
            resourceMetrics = ResourceMetrics.newBuilder().setResource(resource);
            scopes.put(resourceMetrics, null);
        }

        List<ScopeMetrics.Builder> scopesMetrics = scopes.get(resourceMetrics);
        ScopeMetrics.Builder scopeMetric = null;
        if (scopesMetrics == null) {
            scopesMetrics = new ArrayList<>();
            scopeMetric = ScopeMetrics.newBuilder();
            scopeMetric.setScope(scope);
            scopesMetrics.add(scopeMetric);
            scopes.put(resourceMetrics, scopesMetrics);
        } else {
            for (ScopeMetrics.Builder currentScopeMetric : scopesMetrics) {
                if (currentScopeMetric.getScope().equals(scope)) {
                    scopeMetric = currentScopeMetric;
                    break;
                }
            }
            if (scopeMetric == null) {
                scopeMetric = ScopeMetrics.newBuilder();
                scopeMetric.setScope(scope);
                scopesMetrics.add(scopeMetric);
            }
        }
        scopeMetric.addMetrics(metric);
    }

    public ExportMetricsServiceRequest getExportMetricsServiceRequest() {
        for (Map.Entry<ResourceMetrics.Builder, List<ScopeMetrics.Builder>> entry : scopes.entrySet()) {
            for (ScopeMetrics.Builder scopeMetric: entry.getValue()) {
                entry.getKey().addScopeMetrics(scopeMetric);
            }
        }
        for (ResourceMetrics.Builder rsb : scopes.keySet()) {
            exportMetricsServiceRequestBuilder.addResourceMetrics(rsb.build());
        }

        return exportMetricsServiceRequestBuilder.build();
    }

}

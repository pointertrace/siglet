package com.siglet.container.engine.pipeline.accumulator;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpansAccumulator {


    private final Map<ResourceSpans.Builder, List<ScopeSpans.Builder>> scopes = new HashMap<>();

    private final ExportTraceServiceRequest.Builder exportTraceServiceRequestBuilder =
            ExportTraceServiceRequest.newBuilder();

    public void add(Span span, InstrumentationScope scope, Resource resource) {

        ResourceSpans.Builder resourceSpans = null;
        for (ResourceSpans.Builder currentResourceSpans : scopes.keySet()) {
            if (currentResourceSpans.getResource().equals(resource)) {
                resourceSpans = currentResourceSpans;
                break;
            }
        }
        if (resourceSpans == null) {
            resourceSpans = ResourceSpans.newBuilder().setResource(resource);
            scopes.put(resourceSpans,null);
        }

        List<ScopeSpans.Builder> scopesSpans = scopes.get(resourceSpans);
        ScopeSpans.Builder scopeSpan = null;
        if (scopesSpans == null) {
            scopesSpans = new ArrayList<>();
            scopeSpan = ScopeSpans.newBuilder();
            scopeSpan.setScope(scope);
            scopesSpans.add(scopeSpan);
            scopes.put(resourceSpans,scopesSpans);
        } else {
            for (ScopeSpans.Builder currentScopeSpan : scopesSpans) {
                if (currentScopeSpan.getScope().equals(scope)) {
                    scopeSpan = currentScopeSpan;
                    break;
                }
            }
            if (scopeSpan == null) {
                scopeSpan = ScopeSpans.newBuilder();
                scopeSpan.setScope(scope);
                scopesSpans.add(scopeSpan);
            }
        }
        scopeSpan.addSpans(span);

    }



    public ExportTraceServiceRequest getExportTraceServiceRequest() {
        for(Map.Entry<ResourceSpans.Builder, List<ScopeSpans.Builder>> entry : scopes.entrySet()) {
            for(ScopeSpans.Builder scopeSpan : entry.getValue()) {
                entry.getKey().addScopeSpans(scopeSpan);
            }
        }
        for(ResourceSpans.Builder rsb : scopes.keySet()) {
            exportTraceServiceRequestBuilder.addResourceSpans(rsb.build());
        }

        return exportTraceServiceRequestBuilder.build();
    }

}

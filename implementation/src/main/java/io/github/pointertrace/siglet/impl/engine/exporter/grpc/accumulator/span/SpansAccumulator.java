package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.HashMap;
import java.util.Map;

public class SpansAccumulator {

    private final Map<Resource, Map<InstrumentationScope, ScopeSpans.Builder>> scopes = new HashMap<>();

    private final ExportTraceServiceRequest.Builder exportTraceServiceRequestBuilder =
            ExportTraceServiceRequest.newBuilder();

    public void add(Span span, InstrumentationScope scope, Resource resource) {
        Map<InstrumentationScope, ScopeSpans.Builder> scopeMap =
            scopes.computeIfAbsent(resource, k -> new HashMap<>());

        ScopeSpans.Builder scopeSpan =
            scopeMap.computeIfAbsent(scope, k -> ScopeSpans.newBuilder().setScope(scope));

        scopeSpan.addSpans(span);

    }



    public ExportTraceServiceRequest getExportTraceServiceRequest() {
        for (Map.Entry<Resource, Map<InstrumentationScope, ScopeSpans.Builder>> resourceEntry : scopes.entrySet()) {
            ResourceSpans.Builder rsBuilder = ResourceSpans.newBuilder().setResource(resourceEntry.getKey());
            for (ScopeSpans.Builder scopeSpan : resourceEntry.getValue().values()) {
                rsBuilder.addScopeSpans(scopeSpan);
            }
            exportTraceServiceRequestBuilder.addResourceSpans(rsBuilder.build());
        }

        return exportTraceServiceRequestBuilder.build();
    }

}

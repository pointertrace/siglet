package io.github.pointertrace.siglet.container.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.api.Signal;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;

public class AccumulatedSpans implements Signal {

    private final ExportTraceServiceRequest request;

    private final String id;

    public AccumulatedSpans(ExportTraceServiceRequest request, String id) {
        this.request = request;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public ExportTraceServiceRequest getRequest() {
        return request;
    }
}

package com.siglet.container.engine.pipeline.accumulator;

import com.siglet.api.Signal;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;

public class AccumulatedMetrics implements Signal {

    private final ExportMetricsServiceRequest request;

    private final String id;

    public AccumulatedMetrics(ExportMetricsServiceRequest request, String id) {
        this.request = request;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public ExportMetricsServiceRequest getRequest() {
        return request;
    }
}

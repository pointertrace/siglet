package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric;

import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;

public class AccumulatedMetrics {

    private final ExportMetricsServiceRequest request;

    private final int numSignals;

    public AccumulatedMetrics(ExportMetricsServiceRequest request, int numSignals) {
        this.request = request;
        this.numSignals = numSignals;
    }


    public ExportMetricsServiceRequest getRequest() {
        return request;
    }

    public int getNumSignals() {
        return numSignals;
    }
}

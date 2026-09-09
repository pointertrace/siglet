package io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;

public class AccumulatedSpans {

    private final ExportTraceServiceRequest request;

    private final int numSignals;

    public AccumulatedSpans(ExportTraceServiceRequest request, int numSignals) {
        this.request = request;
        this.numSignals = numSignals;
    }
    public ExportTraceServiceRequest getRequest() {
        return request;
    }

    public int getNumSignals() {
        return numSignals;
    }
}

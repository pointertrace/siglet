package com.siglet.container.engine.receiver.grpc;

import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.ArrayList;
import java.util.List;

public class OtelGrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

    private final Context context;

    private final List<SignalDestination> spanDestinations = new ArrayList<>();

    public OtelGrpcTraceService(Context context) {
        this.context = context;
    }

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        for (ResourceSpans spans : request.getResourceSpansList()) {
            Resource resource = spans.getResource();
            for (ScopeSpans scopeSpans : spans.getScopeSpansList()) {
                InstrumentationScope instrumentationScope = scopeSpans.getScope();
                for (Span span : scopeSpans.getSpansList()) {
                    for (SignalDestination destination : spanDestinations) {
                        ProtoSpanAdapter protoSpanAdapter = context.getSpanObjectPool().get(span,
                                instrumentationScope, resource);
                        destination.send(protoSpanAdapter);
                    }
                }
            }
        }

        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void addDestination(SignalDestination destination) {
        spanDestinations.add(destination);
    }

}

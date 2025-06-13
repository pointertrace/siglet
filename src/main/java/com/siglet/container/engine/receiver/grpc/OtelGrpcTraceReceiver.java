package com.siglet.container.engine.receiver.grpc;

import com.siglet.api.Signal;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.receiver.Receiver;
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

public class OtelGrpcTraceReceiver extends TraceServiceGrpc.TraceServiceImplBase
        implements Receiver {

    private final GrpcServer server;

    private final ReceiverNode node;

    private final List<SignalDestination<Signal>> spanDestinations = new ArrayList<>();

    public OtelGrpcTraceReceiver(GrpcServer server, ReceiverNode node) {
        this.server = server;
        this.node = node;
    }

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        for (ResourceSpans spans : request.getResourceSpansList()) {
            Resource resource = spans.getResource();
            for (ScopeSpans scopeSpans : spans.getScopeSpansList()) {
                InstrumentationScope instrumentationScope = scopeSpans.getScope();
                for (Span span : scopeSpans.getSpansList()) {
                    System.out.println("span received trace:" + AdapterUtils.traceIdEx(span.getTraceId())
                            + " spanId:" + AdapterUtils.spanIdEx(span.getSpanId()));
                    for (SignalDestination<Signal> destination : spanDestinations) {
                        destination.send(new ProtoSpanAdapter(span, resource.toBuilder().build(),
                                instrumentationScope.toBuilder().build(), true));

                    }
                }
            }
        }

        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void connect(SignalDestination<Signal> destination) {
        spanDestinations.add(destination);
    }

    @Override
    public synchronized void start() {
        if (server.getState() == State.CREATED) {
            server.start();
        }
    }

    @Override
    public synchronized void stop() {
        if (server.getState() == State.RUNNING) {
            server.stop();
        }
    }

    @Override
    public State getState() {
        return server.getState();
    }

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public ReceiverNode getNode() {
        return null;
    }
}

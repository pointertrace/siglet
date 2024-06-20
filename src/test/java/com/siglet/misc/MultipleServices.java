package com.siglet.misc;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

import java.net.InetSocketAddress;

public class MultipleServices extends TraceServiceGrpc.TraceServiceImplBase {

    public static void main(String[] args) throws Exception {
        Server server;

        var builder = NettyServerBuilder
                .forAddress(new InetSocketAddress("localhost", 8080))
                .addService(new MultipleServices())
                .addService(new MultipleServices());
        server = builder.build();
        server.start();
    }


    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        for (ResourceSpans spans : request.getResourceSpansList()) {
            Resource resource = spans.getResource();
            for (ScopeSpans scopeSpans : spans.getScopeSpansList()) {
                InstrumentationScope instrumentationScope = scopeSpans.getScope();
                for (Span span : scopeSpans.getSpansList()) {
                    System.out.println("spanId:" + span.getSpanId());
                }
            }
        }
        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }}


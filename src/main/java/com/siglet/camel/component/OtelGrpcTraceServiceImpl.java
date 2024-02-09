package com.siglet.camel.component;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.support.DefaultExchange;

public class OtelGrpcTraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

    private final SigletConsumer sigletConsumer;

    public OtelGrpcTraceServiceImpl(SigletConsumer sigletConsumer) {
        this.sigletConsumer = sigletConsumer;
    }

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        for (ResourceSpans spans : request.getResourceSpansList()) {
            Resource resource = spans.getResource();
            for(ScopeSpans scopeSpans : spans.getScopeSpansList()) {
                InstrumentationScope instrumentationScope = scopeSpans.getScope();
                for(Span span:scopeSpans.getSpansList()) {
                    Exchange exchange = new DefaultExchange(sigletConsumer.getEndpoint(), ExchangePattern.InOnly);
//                    exchange.getIn().setBody(new SpanAdapter(span, resource, instrumentationScope));
                    try {
                        sigletConsumer.getProcessor().process(exchange);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

package com.siglet.camel.component;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

public class OtelGrpcMetricsServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

    private final SigletConsumer sigletConsumer;

    public OtelGrpcMetricsServiceImpl(SigletConsumer sigletConsumer) {
        this.sigletConsumer = sigletConsumer;
    }

//    @Override
    public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
//        for (ResourceMetrics resourceMetric : request.getResourceMetricsList()) {
//            Resource resource = resourceMetric.getResource();
//            for (ScopeMetrics scopeMetrics : resourceMetric.getScopeMetricsList()) {
//                InstrumentationScope instrumentationScope = scopeMetrics.getScope();
//                for (Metric metric : scopeMetrics.getMetricsList())) {
//                    Exchange exchange = new DefaultExchange(sigletConsumer.getEndpoint(), ExchangePattern.InOnly);
//                    exchange.getIn().setBody(new ProtoSpanAdapter(span, resource.toBuilder().build(), instrumentationScope.toBuilder().build(), true));
//                    try {
//                        sigletConsumer.getProcessor().process(exchange);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
//        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
    }
}

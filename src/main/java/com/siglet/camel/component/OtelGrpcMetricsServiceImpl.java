package com.siglet.camel.component;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.support.DefaultExchange;

public class OtelGrpcMetricsServiceImpl extends MetricsServiceGrpc.MetricsServiceImplBase {

    private final SigletConsumer sigletConsumer;

    public OtelGrpcMetricsServiceImpl(SigletConsumer sigletConsumer) {
        this.sigletConsumer = sigletConsumer;
    }

    @Override
    public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
        for (ResourceMetrics resourceMetric : request.getResourceMetricsList()) {
            Resource resource = resourceMetric.getResource();
            for (ScopeMetrics scopeMetrics : resourceMetric.getScopeMetricsList()) {
                InstrumentationScope instrumentationScope = scopeMetrics.getScope();
                for (Metric metric : scopeMetrics.getMetricsList()) {
                    Exchange exchange = new DefaultExchange(sigletConsumer.getEndpoint(), ExchangePattern.InOnly);
                    if (metric.hasGauge()) {
                        System.out.println("metric received:" + metric);
                        // TODO verifica se precisa de resource e scope builder/
                        exchange.getIn().setBody(new ProtoMetricAdapter(metric, resource, instrumentationScope, true));
                        try {
                            sigletConsumer.getProcessor().process(exchange);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        ExportMetricsServiceResponse response = ExportMetricsServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

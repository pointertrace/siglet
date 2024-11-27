package com.siglet.camel.component.otelgrpc;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.adapter.trace.ProtoTraceAdapter;
import io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.io.IOException;

public class SigletProducer extends DefaultProducer {


    TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;
    MetricsServiceGrpc.MetricsServiceBlockingStub metricServicesStub;


    public SigletProducer(Endpoint endpoint) {
        super(endpoint);
        System.out.println("producer criado uri " + endpoint.getEndpointUri());
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        Object body = exchange.getIn().getBody();

        if (body instanceof ProtoSpanAdapter spanAdapter) {

            Span span = spanAdapter.getUpdated();
            System.out.println("sending ----------------");
            System.out.println("traceId:" + spanAdapter.getTraceIdEx());
            System.out.println("----------------");
            Resource resource = spanAdapter.getUpdatedResource();
            InstrumentationScope instrumentationScope = spanAdapter.getUpdatedInstrumentationScope();
            ExportTraceServiceRequest exportTraceServiceRequest = ExportTraceServiceRequest.newBuilder()
                    .addResourceSpans(ResourceSpans.newBuilder()
                            .setResource(resource)
                            .addScopeSpans(ScopeSpans.newBuilder()
                                    .setScope(instrumentationScope)
                                    .addSpans(span))
                            .build())
                    .build();

            ExportTraceServiceResponse resp = traceServiceStub.export(exportTraceServiceRequest);
        } else if (body instanceof ProtoTraceAdapter traceAdapter) {

            System.out.println("span[0].id =" + traceAdapter.getAt(0).getSpanIdEx());
            traceAdapter.forEachSpan(modifiableSpan -> {

                ProtoSpanAdapter spanAdapter = (ProtoSpanAdapter) modifiableSpan;
                Span span = spanAdapter.getUpdated();
                System.out.println("sending ----------------");
                System.out.println("traceId:" + spanAdapter.getTraceIdEx());
                System.out.println("----------------");
                Resource resource = spanAdapter.getUpdatedResource();
                InstrumentationScope instrumentationScope = spanAdapter.getUpdatedInstrumentationScope();
                ExportTraceServiceRequest exportTraceServiceRequest = ExportTraceServiceRequest.newBuilder()
                        .addResourceSpans(ResourceSpans.newBuilder()
                                .setResource(resource)
                                .addScopeSpans(ScopeSpans.newBuilder()
                                        .setScope(instrumentationScope)
                                        .addSpans(span))
                                .build())
                        .build();

                ExportTraceServiceResponse resp = traceServiceStub.export(exportTraceServiceRequest);
            });
        } else if (body instanceof ProtoMetricAdapter metricAdapter) {

            System.out.println("metric.name=" + metricAdapter.getName());
            Metric metric = metricAdapter.getUpdated();
            Resource resource = metricAdapter.getUpdatedResource();
            InstrumentationScope instrumentationScope = metricAdapter.getUpdatedInstrumentationScope();

            ExportMetricsServiceRequest exportMetricsServiceRequest = ExportMetricsServiceRequest.newBuilder()
                    .addResourceMetrics(ResourceMetrics.newBuilder()
                            .setResource(resource)
                            .addScopeMetrics(ScopeMetrics.newBuilder()
                                    .setScope(instrumentationScope)
                                    .addMetrics(metric)
                                    .build())
                            .build())
                    .build();

            ExportMetricsServiceResponse resp = metricServicesStub.export(exportMetricsServiceRequest);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        try {
            var builder = NettyChannelBuilder
                    .forAddress(((SigletEndpoint) getEndpoint()).getSocketAddress())
                    .usePlaintext();

            traceServiceStub = TraceServiceGrpc.newBlockingStub(builder.build());
            metricServicesStub = MetricsServiceGrpc.newBlockingStub(builder.build());
        } catch (RuntimeException e) {
            System.out.println("error:" + e);
        }
    }
}

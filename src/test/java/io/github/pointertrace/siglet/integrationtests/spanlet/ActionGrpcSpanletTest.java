package io.github.pointertrace.siglet.integrationtests.spanlet;

import io.github.pointertrace.siglet.container.Siglet;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Disabled
class ActionGrpcSpanletTest {

    private List<Span> receivedSpans;

    private List<Metric> receivedMetrics;

    private Server server;

    private ManagedChannel clientChannel;

    TraceServiceGrpc.TraceServiceBlockingStub traceClient;

    MetricsServiceGrpc.MetricsServiceBlockingStub metricClient;

    @BeforeEach
    void setUp() throws Exception {

        receivedSpans = new ArrayList<>();

        receivedMetrics = new ArrayList<>();

        server = ServerBuilder.forPort(4318)
                .addService(new SpanService(receivedSpans))
                .addService(new MetricService(receivedMetrics))
                .build();

        server.start();

        clientChannel = ManagedChannelBuilder.forAddress("localhost", 4317)
                .usePlaintext()
                .build();


        traceClient = TraceServiceGrpc.newBlockingStub(clientChannel);

        metricClient = MetricsServiceGrpc.newBlockingStub(clientChannel);

    }

    @AfterEach
    void tearDown() {

        if (clientChannel != null && !clientChannel.isShutdown()) {
            clientChannel.shutdown();
        }

        if (server != null && !server.isShutdown()) {
            server.shutdown();
        }

    }

    @Test
    void test() throws Exception {

        String config = """
                receivers:
                - grpc: receiver
                  address: localhost:4317
                exporters:
                - grpc: exporter
                  address: localhost:4318
                pipelines:
                - name: pipeline
                  from: receiver
                  start:
                  - spanlet
                  - metriclet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    thread-pool-size: 1
                    config:
                      action: signal.name = signal.name +"-span-suffix"
                  - name: metriclet
                    kind: metriclet
                    to: exporter
                    type: groovy-action
                    thread-pool-size: 1
                    config:
                      action: signal.name = signal.name +"-metric-suffix"
                """;


        Siglet siglet = new Siglet(config);

        siglet.start();

        generateSignals();

        Thread.sleep(300);

        siglet.stop();

        assertEquals(1, receivedSpans.size());
        assertEquals("span-name-span-suffix", receivedSpans.getFirst().getName());
        assertEquals(1, receivedMetrics.size());
        assertEquals("metric-name-metric-suffix", receivedMetrics.getFirst().getName());

    }

    public void generateSignals() {
        Resource resource = Resource.newBuilder()
                .build();

        InstrumentationScope scope = InstrumentationScope.newBuilder()
                .setName("scope")
                .build();

        Span span = Span.newBuilder()
                .setName("span-name")
                .build();

        ResourceSpans resourceSpans = ResourceSpans.newBuilder()
                .setResource(resource)
                .addScopeSpans(ScopeSpans.newBuilder()
                        .setScope(scope)
                        .addSpans(span)
                        .build())
                .build();

        ExportTraceServiceRequest traceRequest = ExportTraceServiceRequest.newBuilder()
                .addResourceSpans(resourceSpans)
                .build();

        traceClient.export(traceRequest);

        Metric metric = Metric.newBuilder()
                .setName("metric-name")
                .setGauge(Gauge.newBuilder()
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setAsInt(10)
                                .build())
                        .build())
                .build();

        ResourceMetrics resourceMetrics = ResourceMetrics.newBuilder()
                .setResource(resource)
                .addScopeMetrics(ScopeMetrics.newBuilder()
                        .setScope(scope)
                        .addMetrics(metric)
                        .build())
                .build();

        ExportMetricsServiceRequest metricRequest = ExportMetricsServiceRequest.newBuilder()
                .addResourceMetrics(resourceMetrics)
                .build();

        metricClient.export(metricRequest);
    }

    static class MetricService extends MetricsServiceGrpc.MetricsServiceImplBase {

        private List<Metric> metrics;


        public MetricService(List<Metric> metrics) {
            this.metrics = metrics;
        }

        @Override
        public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
            for (ResourceMetrics resourceMetric : request.getResourceMetricsList()) {
                for (ScopeMetrics scopeMetrics : resourceMetric.getScopeMetricsList()) {
                    for (Metric metric : scopeMetrics.getMetricsList()) {
                        metrics.add(metric);
                    }
                }
            }
            ExportMetricsServiceResponse response = ExportMetricsServiceResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

    static class SpanService extends TraceServiceGrpc.TraceServiceImplBase {

        private List<Span> spans;


        public SpanService(List<Span> spans) {
            this.spans = spans;
        }

        @Override
        public void export(ExportTraceServiceRequest request,
                           StreamObserver<ExportTraceServiceResponse> responseObserver) {
            for (ResourceSpans resourceSpan : request.getResourceSpansList()) {
                for (ScopeSpans scopeSpans : resourceSpan.getScopeSpansList()) {
                    for (Span span : scopeSpans.getSpansList()) {
                        spans.add(span);
                    }
                }
            }
            ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }
}

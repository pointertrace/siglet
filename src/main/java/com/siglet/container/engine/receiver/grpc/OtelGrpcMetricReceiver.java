package com.siglet.container.engine.receiver.grpc;

import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.receiver.Receiver;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.metrics.v1.ScopeMetrics;
import io.opentelemetry.proto.resource.v1.Resource;

import java.util.ArrayList;
import java.util.List;

public class OtelGrpcMetricReceiver extends MetricsServiceGrpc.MetricsServiceImplBase
        implements Receiver {

    private final GrpcServer server;

    private final ReceiverNode node;

    private final List<SignalDestination<Signal>> metricDestinations = new ArrayList<>();

    public OtelGrpcMetricReceiver(GrpcServer server, ReceiverNode node) {
        this.server = server;
        this.node = node;
    }


    @Override
    public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
        for (ResourceMetrics resourceMetric : request.getResourceMetricsList()) {
            Resource resource = resourceMetric.getResource();
            for (ScopeMetrics scopeMetrics : resourceMetric.getScopeMetricsList()) {
                InstrumentationScope instrumentationScope = scopeMetrics.getScope();
                for (Metric metric : scopeMetrics.getMetricsList()) {
                    if (metric.hasGauge()) {
                        System.out.println("metric received:" + metric);
                        // TODO verifica se precisa de resource e scope builder/
                        for (SignalDestination<Signal> destination : metricDestinations) {
                            destination.send(new ProtoMetricAdapter(metric, resource, instrumentationScope));
                        }
                    }
                }
            }
        }
        ExportMetricsServiceResponse response = ExportMetricsServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void connect(SignalDestination<Signal> signalDestination) {
        metricDestinations.add(signalDestination);
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
        return node;
    }
}

package com.siglet.container.engine.receiver.grpc;

import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
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

public class OtelGrpcMetricService extends MetricsServiceGrpc.MetricsServiceImplBase {

    private final Context context;

    private final List<SignalDestination> metricDestinations = new ArrayList<>();

    public OtelGrpcMetricService(Context context) {
        this.context = context;
    }


    @Override
    public void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> responseObserver) {
        for (ResourceMetrics resourceMetric : request.getResourceMetricsList()) {
            Resource resource = resourceMetric.getResource();
            for (ScopeMetrics scopeMetrics : resourceMetric.getScopeMetricsList()) {
                InstrumentationScope instrumentationScope = scopeMetrics.getScope();
                for (Metric metric : scopeMetrics.getMetricsList()) {
                    if (metric.hasGauge()) {
                        for (SignalDestination destination : metricDestinations) {
                                ProtoMetricAdapter protoMetricAdapter =
                                        context.getMetricObjectPool().get(metric, instrumentationScope, resource);
                                destination.send(protoMetricAdapter);
                        }
                    }
                }
            }
        }
        ExportMetricsServiceResponse response = ExportMetricsServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void addDestination(SignalDestination destination) {
        metricDestinations.add(destination);
    }
}

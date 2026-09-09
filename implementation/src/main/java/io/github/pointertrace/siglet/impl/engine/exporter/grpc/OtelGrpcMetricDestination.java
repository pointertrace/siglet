package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric.AccumulatedMetrics;
import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OtelGrpcMetricDestination {

    private static final Logger LOGGER = LogManager.getLogger(OtelGrpcMetricDestination.class);

    private final MetricsServiceGrpc.MetricsServiceStub metricsServiceStub;

    private final LongCounter signalsEmitterCounter;

    private final LongGauge senderPackageSizeGauge;

    public OtelGrpcMetricDestination(OtelGrpcExporter exporter, MetricsServiceGrpc.MetricsServiceStub metricsServiceStub) {
        this.metricsServiceStub = metricsServiceStub;
        this.signalsEmitterCounter = exporter.getSigletContext().getMetrics()
                .createEmittedSignalsCounter(exporter.getName());
        this.senderPackageSizeGauge = exporter.getSigletContext().getMetrics().createGrpcSenderPackageSizeGauge(
                exporter.getName(),
                "metrics"
        );
    }


    public boolean send(AccumulatedMetrics accumulatedMetrics) {
        try {
            senderPackageSizeGauge.set(accumulatedMetrics.getNumSignals());
            metricsServiceStub.export(
                    accumulatedMetrics.getRequest(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(ExportMetricsServiceResponse response) {
                            signalsEmitterCounter.increment();
                        }

                        @Override
                        public void onError(Throwable t) {
                            System.out.println("######################### Error sending metrics: " + t.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                        }
                    }
            );
            return true;
        } catch (io.grpc.StatusRuntimeException e) {
            LOGGER.error("Error sending metrics code:{} message:{}", e.getStatus().getCode(),
                    e.getStatus().getDescription(), e);
            return false;
        }
    }
}

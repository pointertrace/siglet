package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span.AccumulatedSpans;
import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OtelGrpcSpanDestination {

    private static final Logger LOGGER = LogManager.getLogger(OtelGrpcSpanDestination.class);

    private final TraceServiceGrpc.TraceServiceStub traceServiceStub;

    private final LongCounter signalsEmitterCounter;

    private final LongGauge senderPackageSizeGauge;

    public OtelGrpcSpanDestination(OtelGrpcExporter exporter, TraceServiceGrpc.TraceServiceStub traceServiceStub) {
        this.traceServiceStub = traceServiceStub;
        this.signalsEmitterCounter = exporter.getSigletContext().getMetrics()
                .createEmittedSignalsCounter(exporter.getName());
        this.senderPackageSizeGauge = exporter.getSigletContext().getMetrics().createGrpcSenderPackageSizeGauge(
                exporter.getName(),
                "traces"
        );
    }


    public boolean send(AccumulatedSpans accumulatedSpans) {
        try {
            senderPackageSizeGauge.set(accumulatedSpans.getNumSignals());
            traceServiceStub.export(
                    accumulatedSpans.getRequest(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(ExportTraceServiceResponse response) {
                            signalsEmitterCounter.increment();
                        }

                        @Override
                        public void onError(Throwable t) {
                        }

                        @Override
                        public void onCompleted() {
                        }
                    }
            );
            return true;
        } catch (io.grpc.StatusRuntimeException e) {
            LOGGER.error("Error sending spans code:{} message:{}", e.getStatus().getCode(),
                    e.getStatus().getDescription(), e);
            return false;
        }
    }
}

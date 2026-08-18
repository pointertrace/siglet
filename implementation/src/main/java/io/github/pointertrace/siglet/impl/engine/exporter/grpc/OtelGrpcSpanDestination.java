package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.AccumulatedSpans;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OtelGrpcSpanDestination {

    private static final Logger LOGGER = LogManager.getLogger(OtelGrpcSpanDestination.class);

    private final TraceServiceGrpc.TraceServiceStub traceServiceStub;

    public OtelGrpcSpanDestination(TraceServiceGrpc.TraceServiceStub traceServiceStub) {
        this.traceServiceStub = traceServiceStub;
    }


    public boolean send(AccumulatedSpans accumulatedSpans) {
        try {
            traceServiceStub.export(
                    accumulatedSpans.getRequest(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(ExportTraceServiceResponse response) {
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

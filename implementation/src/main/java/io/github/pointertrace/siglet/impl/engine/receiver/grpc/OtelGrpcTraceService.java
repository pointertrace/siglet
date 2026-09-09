package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.engine.metric.LongCounter;
import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventLoop;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

public class OtelGrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

    private final ProcessorEventLoop<OtelGrpcReceiver.TraceServiceRequest,?> eventLoop;

    private final LongCounter receivedSignalsCounter;


    public OtelGrpcTraceService(LongCounter receivedSignalsCounter, ProcessorEventLoop<OtelGrpcReceiver.TraceServiceRequest,?> eventLoop) {
        this.receivedSignalsCounter = receivedSignalsCounter;
        this.eventLoop = eventLoop;
    }

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        receivedSignalsCounter.increment();
        // todo acessar direto
        LongGauge grpcPackageSizeGauge = OtelGrpcReceiver.GrpcContexts.GRPC_PACKAGE_SIZE_GAUGE.get();
        eventLoop.getReceiver().receive(new OtelGrpcReceiver.TraceServiceRequest(request, grpcPackageSizeGauge));
        responseObserver.onNext(ExportTraceServiceResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

}

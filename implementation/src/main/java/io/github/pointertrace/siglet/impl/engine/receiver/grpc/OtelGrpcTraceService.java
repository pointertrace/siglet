package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventLoop;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

public class OtelGrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

    private final ProcessorEventLoop eventLoop;


    public OtelGrpcTraceService(ProcessorEventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    @Override
    public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {
        eventLoop.receive(request);
        ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}

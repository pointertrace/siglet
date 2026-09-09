package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.github.pointertrace.siglet.impl.engine.metric.Metrics;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.grpc.*;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrpcTracePackageMetricsInterceptor implements ServerInterceptor {

    private static final String TRACE_METHOD_NAME = TraceServiceGrpc.getExportMethod().getFullMethodName();

    private final OtelGrpcReceiver otelGrpcReceiver;

    private final Map<String, LongGauge> clientMetrics = new ConcurrentHashMap<>();

    private final Metrics metrics;


    public GrpcTracePackageMetricsInterceptor(OtelGrpcReceiver otelGrpcReceiver) {
        this.otelGrpcReceiver = otelGrpcReceiver;
        this.metrics = otelGrpcReceiver.getSigletContext().getMetrics();

    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        if (TRACE_METHOD_NAME.equals(call.getMethodDescriptor().getFullMethodName())) {
            String clientId = identifyClient(call);
            LongGauge grpcPackageSizeGauge = clientMetrics.computeIfAbsent(clientId, cId ->
                    metrics.createGrpcReceiverPackageSizeGauge(otelGrpcReceiver.getName(), cId, "traces"));

            Context context = Context.current()
                    .withValue(
                            OtelGrpcReceiver.GrpcContexts.GRPC_PACKAGE_SIZE_GAUGE,
                            grpcPackageSizeGauge);

            return Contexts.interceptCall(
                    context,
                    call,
                    headers,
                    next);
        }
        return next.startCall(call, headers);
    }

    private String identifyClient(ServerCall<?, ?> call) {
        SocketAddress remoteAddress =
                call.getAttributes()
                        .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        if (remoteAddress == null) {
            return "unknown";
        }
        return remoteAddress.toString();
    }
}


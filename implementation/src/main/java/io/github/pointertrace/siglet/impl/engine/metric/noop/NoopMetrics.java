package io.github.pointertrace.siglet.impl.engine.metric.noop;

import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.metric.*;
import io.grpc.*;

public class NoopMetrics implements Metrics {

    @Override
    public LongCounter createDroppedSignalsCounter(String componentName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createMissedSignalsCounter(String sourceName, String destinationName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createAcceptedSignalsCounter(String sourceName, String destinationName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createReceivedSignalsCounter(String componentName) {
        return new NoopLongCounter();
    }

    @Override
    public LongGauge createGrpcReceiverPackageSizeGauge(String componentName, String client, String signalType) {
        return new NoopLongGauge();
    }

    @Override
    public LongGauge createGrpcSenderPackageSizeGauge(String componentName, String signalType) {
        return new NoopLongGauge();
    }

    @Override
    public LongCounter createQueueMissedSignalsCounter(String parentComponent, String eventLoopName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createQueueAcceptedSignalsCounter(String parentComponent, String eventLoopName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createQueueReceivedSignalsCounter(String parentComponent, String eventLoopName) {
        return new NoopLongCounter();
    }

    @Override
    public LongCounter createEmittedSignalsCounter(String componentName) {
        return new NoopLongCounter();
    }

    @Override
    public void createQueueMetrics(String parentComponent, String eventLoop, MeteredBlockingQueue<?> queue) {
    }

    @Override
    public LongTimer createEventLoopProcessFunctionTimer(String parent, String eventLoop) {
        return new NoopLongTimer();
    }

    @Override
    public LongTimer createQueueWaitTimer(String parent, String eventLoop) {
        return new NoopLongTimer();
    }

    @Override
    public ServerInterceptor createGrpcServerMetricsInterceptor(String componentName) {
        return new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                return next.startCall(call, headers);
            }
        };
    }

    @Override
    public ClientInterceptor createGrpcClientMetricsInterceptor(String componentName) {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                return next.newCall(method, callOptions);
            }
        };
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public State getState() {
        return State.RUNNING;
    }

    @Override
    public String getName() {
        return "noop-metrics";
    }
}

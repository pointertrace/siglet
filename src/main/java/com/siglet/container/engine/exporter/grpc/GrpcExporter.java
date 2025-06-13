package com.siglet.container.engine.exporter.grpc;

import com.siglet.api.Signal;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.raw.GrpcExporterConfig;
import com.siglet.container.engine.State;
import com.siglet.container.engine.exporter.Exporter;
import io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

public class GrpcExporter implements Exporter {

    private TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;
    private MetricsServiceGrpc.MetricsServiceBlockingStub metricServicesStub;

    private final ExporterNode node;

    public GrpcExporter(ExporterNode node) {
        this.node = node;
    }

    @Override
    public boolean send(Signal signal) {
        throw new IllegalStateException("to be implemented!");
    }

    @Override
    public Class<Signal> getType() {
        return Signal.class;
    }

    @Override
    public void start() {
        var builder = NettyChannelBuilder
                .forAddress((getConfig().getAddress()))
                .usePlaintext();

        traceServiceStub = TraceServiceGrpc.newBlockingStub(builder.build());
        metricServicesStub = MetricsServiceGrpc.newBlockingStub(builder.build());
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
        return node.getName();
    }

    public GrpcExporterConfig getConfig() {
        return (GrpcExporterConfig) node.getConfig();
    }

    @Override
    public ExporterNode getNode() {
        return node;
    }
}

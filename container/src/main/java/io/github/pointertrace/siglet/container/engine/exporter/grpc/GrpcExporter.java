package io.github.pointertrace.siglet.container.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.config.graph.ExporterNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.config.raw.GrpcExporterConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.exporter.Exporter;
import io.github.pointertrace.siglet.container.engine.pipeline.accumulator.AccumulatedMetrics;
import io.github.pointertrace.siglet.container.engine.pipeline.accumulator.AccumulatedSpans;
import io.github.pointertrace.siglet.container.engine.pipeline.accumulator.MetricAccumulator;
import io.github.pointertrace.siglet.container.engine.pipeline.accumulator.SpanAccumulator;
import io.github.pointertrace.siglet.container.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

import java.util.Set;

public class GrpcExporter implements Exporter {

    private final ExporterNode node;

    private final TimeoutAccumulatorEventLoop spanAccumulator;

    private final TimeoutAccumulatorEventLoop metricAccumulator;

    private State state = State.RUNNING;

    public GrpcExporter(Context context, ExporterNode node) {
        this.node = node;
        GrpcExporterConfig config = (GrpcExporterConfig) node.getConfig();
        spanAccumulator = new TimeoutAccumulatorEventLoop(
                node.getName() + "-span",
                config.getQueueSizeConfig().getQueueSize(),
                config.getBatchTimeoutInMillis(),
                config.getBatchSizeInSignals(),
                span -> SpanAccumulator.accumulateSpans(context, span));


        metricAccumulator = new TimeoutAccumulatorEventLoop(
                node.getName() + "-metric",
                config.getQueueSizeConfig().getQueueSize(),
                config.getBatchTimeoutInMillis(),
                config.getBatchSizeInSignals(),
                metric -> MetricAccumulator.accumulateMetrics(context, metric));

    }


    @Override
    public boolean send(Signal signal) {
        switch (signal) {
            case ProtoSpanAdapter protoSpanAdapter -> spanAccumulator.send(protoSpanAdapter);
            case ProtoMetricAdapter protoMetricAdapter -> metricAccumulator.send(protoMetricAdapter);
            default -> throw new SigletError(String.format("Can only export signals of types %s or %s and not %s.",
                    AccumulatedSpans.class.getName(), AccumulatedMetrics.class.getName(),
                    signal.getClass().getName()));
        }
        return true;
    }

    @Override
    public Set<SignalType> getSignalCapabilities() {
        return Set.of(SignalType.SPAN, SignalType.METRIC);
    }

    @Override
    public synchronized void start() {
        state = State.STARTING;
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(getConfig().getAddress())
                .usePlaintext();

        metricAccumulator.connect(new GrpcMetricDestination(MetricsServiceGrpc.newBlockingStub(builder.build())));
        spanAccumulator.connect(new GrpcSpanDestination(TraceServiceGrpc.newBlockingStub(builder.build())));
        spanAccumulator.start();
        metricAccumulator.start();
        state = State.RUNNING;
    }

    @Override
    public synchronized void stop() {
        state = State.STOPPING;
        spanAccumulator.stop();
        metricAccumulator.stop();
        state = State.STOPPED;
    }

    @Override
    public synchronized State getState() {
        return state;
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

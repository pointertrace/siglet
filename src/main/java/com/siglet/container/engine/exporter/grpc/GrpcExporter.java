package com.siglet.container.engine.exporter.grpc;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.config.graph.ExporterNode;
import com.siglet.container.config.raw.GrpcExporterConfig;
import com.siglet.container.config.raw.SignalType;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.SignalDestination;
import com.siglet.container.engine.State;
import com.siglet.container.engine.exporter.Exporter;
import com.siglet.container.engine.pipeline.accumulator.AccumulatedMetrics;
import com.siglet.container.engine.pipeline.accumulator.AccumulatedSpans;
import com.siglet.container.engine.pipeline.accumulator.MetricAccumulator;
import com.siglet.container.engine.pipeline.accumulator.SpanAccumulator;
import com.siglet.container.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

import java.util.Set;

public class GrpcExporter implements Exporter {

    private TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;
    private MetricsServiceGrpc.MetricsServiceBlockingStub metricServicesStub;

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
        spanAccumulator.connect(new SignalDestination() {
            @Override
            public String getName() {
                return "aggregated-spans";
            }

            @Override
            public boolean send(Signal signal) {
                traceServiceStub.export(((AccumulatedSpans) signal).getRequest());
                return true;
            }

            @Override
            public Set<SignalType> getSignalCapabilities() {
                return Set.of(SignalType.TRACE, SignalType.METRIC);
            }
        });

        metricAccumulator = new TimeoutAccumulatorEventLoop(
                node.getName() + "-metric",
                1_000,
                config.getBatchTimeoutInMillis() == null ? 0 : config.getBatchTimeoutInMillis(),
                config.getBatchSizeInSignals() == null ? 1 : config.getBatchSizeInSignals(),
                MetricAccumulator::accumulate);
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
        return Set.of(SignalType.TRACE, SignalType.METRIC);
    }

    @Override
    public synchronized void start() {
        state = State.STARTING;
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(getConfig().getAddress())
                .usePlaintext();
        traceServiceStub = TraceServiceGrpc.newBlockingStub(builder.build());
        metricServicesStub = MetricsServiceGrpc.newBlockingStub(builder.build());

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

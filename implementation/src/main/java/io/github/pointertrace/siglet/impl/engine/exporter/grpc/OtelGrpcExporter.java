package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.exporter.Exporter;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.AccumulatedMetrics;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.AccumulatedSpans;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.MetricAccumulator;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.SpanAccumulator;
import io.github.pointertrace.siglet.impl.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

public class OtelGrpcExporter implements Exporter {

    private final ExporterNode node;

    private final TimeoutAccumulatorEventLoop spanAccumulator;

    private final TimeoutAccumulatorEventLoop metricAccumulator;

    private State state = State.RUNNING;

    private final SignalCapabilities signalCapabilities = SignalCapabilities.of(Span.class, Metric.class);

    public OtelGrpcExporter(SigletContext sigletContext, ExporterNode node) {
        this.node = node;
        OtelGrpcExporterConfig config = (OtelGrpcExporterConfig) node.getDescription().getConfig();
        spanAccumulator = new TimeoutAccumulatorEventLoop(
                node.getName() + "-span",
                ((OtelGrpcExporterConfig) node.getDescription().getConfig()).getQueueSize().getValue().intValue(),
                config.getBatchTimeoutInMillis().getValue().intValue(),
                config.getBatchSizeInSignals().getValue().intValue(),
                span -> SpanAccumulator.accumulateSpans(sigletContext, span));

        metricAccumulator = new TimeoutAccumulatorEventLoop(
                node.getName() + "-metric",
                ((OtelGrpcExporterConfig) node.getDescription().getConfig()).getQueueSize().getValue().intValue(),
                config.getBatchTimeoutInMillis().getValue().intValue(),
                config.getBatchSizeInSignals().getValue().intValue(),
                metric -> MetricAccumulator.accumulateMetrics(sigletContext, metric));

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
    public SignalCapabilities getIncomingCapabilities() {
        return signalCapabilities;
    }

    @Override
    public synchronized void start() {
        state = State.STARTING;
        NettyChannelBuilder builder = NettyChannelBuilder
                .forAddress(getConfig().getAddress().getInetSocketAddress())
                .usePlaintext();

        metricAccumulator.connect(new OtelGrpcMetricDestination(MetricsServiceGrpc.newBlockingStub(builder.build())));
        spanAccumulator.connect(new OtelGrpcSpanDestination(TraceServiceGrpc.newBlockingStub(builder.build())));
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

    public OtelGrpcExporterConfig getConfig() {
        return (OtelGrpcExporterConfig) node.getDescription().getConfig();
    }

    @Override
    public ExporterNode getNode() {
        return node;
    }
}

package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationImpl;
import io.github.pointertrace.siglet.impl.engine.event.NoopEventBus;
import io.github.pointertrace.siglet.impl.engine.exporter.BaseExporter;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.SpanAccumulator;
import io.github.pointertrace.siglet.impl.engine.pipeline.accumulator.AccumulatedMetrics;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.AccumulatedSpans;
import io.github.pointertrace.siglet.impl.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

import java.net.InetSocketAddress;

public class OtelGrpcExporter extends BaseExporter {

    private TimeoutAccumulatorEventLoop<Object, AccumulatedSpans> spanAccumulator;

    private OtelGrpcSpanDestination otelGrpcSpanDestination;

    private final int queueSize;

    private final int batchSizeInSignals;

    private final int batchTimeoutInMillis;


    public OtelGrpcExporter(SigletContext sigletContext, ExporterNode node) {
        super(sigletContext, node);
        if (node.getDescription().getConfig() instanceof OtelGrpcExporterConfig config) {
            queueSize = sigletContext.getConfig().getQueueSize(config);
            batchSizeInSignals = getConfig().getBatchSizeInSignals().getValue().intValue();
            batchTimeoutInMillis = getConfig().getBatchTimeoutInMillis().getValue().intValue();
        } else {
            throw new SigletError("Invalid config type for OtelGrpcExporter");
        }
    }


    private  boolean send(Object signal) {
        switch (signal) {
            case SpanAdapter spanAdapter -> spanAccumulator.receive(spanAdapter);
//            case MetricAdapter metricAdapter -> metricAccumulator.receive(metricAdapter);
            default -> throw new SigletError(String.format("Can only export signals of types %s or %s and not %s.",
                    AccumulatedSpans.class.getName(), AccumulatedMetrics.class.getName(),
                    signal.getClass().getName()));
        }
        return true;
    }


    @Override
    public void doStart() {
        InetSocketAddress address = getConfig().getAddress().getInetSocketAddress();

        otelGrpcSpanDestination = new OtelGrpcSpanDestination(
                TraceServiceGrpc.newStub(
                        NettyChannelBuilder
                                .forAddress(
                                        address.getHostString(),
                                        address.getPort())
                                .usePlaintext()
                                .build())
        );

        spanAccumulator = new TimeoutAccumulatorEventLoop<>(
                this,
                "span-accumulator",
                queueSize,
                batchSizeInSignals,
                batchTimeoutInMillis,
                Object.class,
                span -> SpanAccumulator.accumulateSpans(span),
                otelGrpcSpanDestination::send,
                new NoopEventBus()
        );
        spanAccumulator.start();
    }

    @Override
    public void doStop() {
        spanAccumulator.stop();
    }

    public OtelGrpcExporterConfig getConfig() {
        return (OtelGrpcExporterConfig) getNode().getDescription().getConfig();
    }

    @Override
    public SignalDestination getSignalDestination() {
        return new SignalDestinationImpl(this, this::send);
    }
}

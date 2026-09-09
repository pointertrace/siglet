package io.github.pointertrace.siglet.impl.engine.exporter.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.graph.ExporterNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestinationImpl;
import io.github.pointertrace.siglet.impl.engine.exporter.BaseExporter;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span.SpanAccumulator;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric.AccumulatedMetrics;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span.AccumulatedSpans;
import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.metric.MetricAccumulator;
import io.github.pointertrace.siglet.impl.eventloop.ReceiveFunction;
import io.github.pointertrace.siglet.impl.eventloop.accumulator.TimeoutAccumulatorEventLoop;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.sdk.metrics.data.MetricData;

import java.net.InetSocketAddress;

public class OtelGrpcExporter extends BaseExporter {

    private TimeoutAccumulatorEventLoop<Object, AccumulatedSpans> spanAccumulator;

    private TimeoutAccumulatorEventLoop<Object, AccumulatedMetrics> metricAccumulator;

    private final int queueSize;

    private final int batchSizeInSignals;

    private final int batchTimeoutInMillis;

    private ReceiveFunction<Object> spanEventLoopReceiverFunction;

    private ReceiveFunction<Object> metricEventLoopReceiverFunction;

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


    public boolean receive(Object signal) {
        switch (signal) {
            case SpanAdapter spanAdapter -> spanEventLoopReceiverFunction.receive(spanAdapter);
            case MetricData metricData -> metricEventLoopReceiverFunction.receive(metricData);
            default -> throw new SigletError(String.format("Can only export signals of types %s or %s and not %s.",
                    AccumulatedSpans.class.getName(), AccumulatedMetrics.class.getName(),
                    signal.getClass().getName()));
        }
        return true;
    }


    @Override
    public void doStart() {
        InetSocketAddress address = getConfig().getAddress().getInetSocketAddress();

        OtelGrpcSpanDestination otelGrpcSpanDestination = new OtelGrpcSpanDestination(
                this,
                TraceServiceGrpc.newStub(
                        NettyChannelBuilder
                                .forAddress(
                                        address.getHostString(),
                                        address.getPort()
                                )
                                .intercept(getSigletContext().getMetrics().createGrpcClientMetricsInterceptor(getName()))
                                .usePlaintext()
                                .build()
                )
        );

        spanAccumulator = new TimeoutAccumulatorEventLoop<>(
                this,
                "pack-spans",
                queueSize,
                batchSizeInSignals,
                batchTimeoutInMillis,
                Object.class,
                span -> SpanAccumulator.accumulateSpans(span),
                otelGrpcSpanDestination::send,
                getInterceptor()
        );
        spanEventLoopReceiverFunction = spanAccumulator.getReceiver();


        OtelGrpcMetricDestination otelGrpcMetricDestination = new OtelGrpcMetricDestination(
                this,
                MetricsServiceGrpc.newStub(
                        NettyChannelBuilder
                                .forAddress(
                                        address.getHostString(),
                                        address.getPort()
                                )
                                .intercept(getSigletContext().getMetrics().createGrpcClientMetricsInterceptor(getName()))
                                .usePlaintext()
                                .build()
                )
        );

        metricAccumulator = new TimeoutAccumulatorEventLoop<>(
                this,
                "pack-metrics",
                queueSize,
                batchSizeInSignals,
                batchTimeoutInMillis,
                Object.class,
                metric -> MetricAccumulator.accumulateMetrics(metric),
                otelGrpcMetricDestination::send,
                getInterceptor()
        );
        metricEventLoopReceiverFunction = metricAccumulator.getReceiver();

        spanAccumulator.start();
        metricAccumulator.start();
    }

    @Override
    public void doStop() {
        spanAccumulator.stop();
        metricAccumulator.stop();
    }

    public OtelGrpcExporterConfig getConfig() {
        return (OtelGrpcExporterConfig) getNode().getDescription().getConfig();
    }

    @Override
    public SignalDestination getSignalDestination() {
        return new SignalDestinationImpl(this, this::receive, getSigletContext().getInterceptor());
    }
}

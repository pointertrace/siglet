package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.adapter.EnqueuedTimeObservable;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSource;
import io.github.pointertrace.siglet.impl.engine.component.connection.SignalSourceImpl;
import io.github.pointertrace.siglet.impl.engine.metric.LongGauge;
import io.github.pointertrace.siglet.impl.engine.metric.otelgrpc.OtelGrpcMetrics;
import io.github.pointertrace.siglet.impl.engine.receiver.BaseReceiver;
import io.github.pointertrace.siglet.impl.eventloop.processor.ProcessorEventLoop;
import io.grpc.Context;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OtelGrpcReceiver extends BaseReceiver {

    private final NettyServerBuilder serverBuilder;

    private Server server;

    private final ProcessorEventLoop<TraceServiceRequest, List<SpanAdapter>> spanEventLoop;

    private SignalEmitterFunction emitterFunction;

    private SignalSource signalSource;

    public OtelGrpcReceiver(SigletContext sigletContext, ReceiverNode receiverNode) {
        super(sigletContext, receiverNode);
        ReceiverDescriptor receiverDescriptor = receiverNode.getDescription();
        if (receiverDescriptor.getConfig() instanceof OtelGrpcReceiverConfig otelGrpcReceiverConfig) {
            spanEventLoop = new ProcessorEventLoop<TraceServiceRequest, List<SpanAdapter>>(this,
                    "unpack-spans",
                    sigletContext.getConfig().getQueueSize(otelGrpcReceiverConfig),
                    sigletContext.getConfig().getThreadPoolSize(otelGrpcReceiverConfig),
                    () -> (TraceServiceRequest request) -> {
                        List<SpanAdapter> spanAdapters = new ArrayList<>(1000);
                        for (ResourceSpans spans : request.getGrpcTraceRequest().getResourceSpansList()) {
                            Resource resource = spans.getResource();
                            for (ScopeSpans scopeSpans : spans.getScopeSpansList()) {
                                InstrumentationScope instrumentationScope = scopeSpans.getScope();
                                for (io.opentelemetry.proto.trace.v1.Span span : scopeSpans.getSpansList()) {
                                    SpanAdapter spanAdapter = new SpanAdapter(span, resource, instrumentationScope);
                                    spanAdapters.add(spanAdapter);
                                }
                            }
                        }
                        request.getPackageSizeGauge().set(spanAdapters.size());
                        return spanAdapters;
                    },
                    this::sendSpans,
                    getInterceptor()
            );
            OtelGrpcTraceService spanService = new OtelGrpcTraceService(
                    getSigletContext().getMetrics().createReceivedSignalsCounter(getName()),
                    spanEventLoop
            );
            serverBuilder = createServerBuilder(otelGrpcReceiverConfig);
            serverBuilder.addService(
                    ServerInterceptors.intercept(
                            spanService,
                            new GrpcTracePackageMetricsInterceptor(this),
                            getSigletContext().getMetrics().createGrpcServerMetricsInterceptor(getName())
                    )
            );
        } else {
            throw new SigletError("Receiver config is not of type " + OtelGrpcReceiverConfig.class.getName());
        }
    }

    private NettyServerBuilder createServerBuilder(OtelGrpcReceiverConfig otelGrpcReceiverConfig) {

        NettyServerBuilder serverBuilder = NettyServerBuilder
                .forAddress(otelGrpcReceiverConfig.getAddress().getInetSocketAddress());


        if (Epoll.isAvailable()) {
            serverBuilder = createEpollServerBuilder(serverBuilder);
        } else {
            serverBuilder = createNioServerBuilder(serverBuilder);
        }
        applyThroughputTuning(serverBuilder, otelGrpcReceiverConfig);

        return serverBuilder;
    }


    private NettyServerBuilder createEpollServerBuilder(NettyServerBuilder serverBuilder) {

        return serverBuilder
                .bossEventLoopGroup(new EpollEventLoopGroup(1))
                .workerEventLoopGroup(new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors()))
                .channelType(EpollServerSocketChannel.class);

//                NettyEventLoopMetrics.register(sigletContext.getMeterRegistry(), workerGroup);

    }

    private NettyServerBuilder createNioServerBuilder(NettyServerBuilder serverBuilder) {

        return serverBuilder
                .bossEventLoopGroup(new NioEventLoopGroup(1))
                .workerEventLoopGroup(new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()))
                .channelType(NioServerSocketChannel.class);


    }

    private static void applyThroughputTuning(NettyServerBuilder builder, OtelGrpcReceiverConfig config) {
        builder.maxInboundMessageSize(config.getMaxInboundMessageSizeBytes().getValue().intValue())
                .maxInboundMetadataSize(config.getMaxInboundMetadataSizeBytes().getValue().intValue())
                .flowControlWindow(config.getFlowControlWindowBytes().getValue().intValue())
                .maxConcurrentCallsPerConnection(config.getMaxConcurrentCallsPerConnection().getValue().intValue())
                .keepAliveTime(config.getKeepAliveTimeSeconds().getValue().longValue(), TimeUnit.SECONDS)
                .keepAliveTimeout(config.getKeepAliveTimeoutSeconds().getValue().longValue(), TimeUnit.SECONDS)
                .permitKeepAliveTime(config.getPermitKeepAliveTimeSeconds().getValue().longValue(), TimeUnit.SECONDS)
                .permitKeepAliveWithoutCalls(true);
    }


    @Override
    public void doStart() {
        try {
            spanEventLoop.start();
            server = serverBuilder.build();
            server.start();
        } catch (IOException e) {
            throw new SigletError("Error starting grpc server " + server + ", " + e.getMessage(), e);
        }
    }


    @Override
    public synchronized void doStop() {
        try {
            spanEventLoop.stop();
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            server.shutdownNow();
        }
    }

    protected void sendSpans(List<? extends SpanAdapter> spans) {

        for (SpanAdapter span : spans) {
            emitterFunction.emit(span, SignalDestination.ALL);
        }
    }

    @Override
    public SignalSource getSignalSource() {
        if (signalSource == null) {
            signalSource = new SignalSourceImpl(this, getSigletContext().getInterceptor());
            this.emitterFunction = signalSource.getSignalEmitterFunction();
        }
        return signalSource;
    }

    public static final class GrpcContexts {

        private GrpcContexts() {
        }

        public static final Context.Key<LongGauge> GRPC_PACKAGE_SIZE_GAUGE =
                Context.key("grpcPackageSizeGauge");
    }

    public static class TraceServiceRequest implements EnqueuedTimeObservable {

        private final ExportTraceServiceRequest grpcTraceRequest;
        private final LongGauge packageSizeGauge;
        private long enqueuedTimeNanos;

        public TraceServiceRequest(ExportTraceServiceRequest grpcTraceRequest, LongGauge packageSizeGauge) {
            this.grpcTraceRequest = grpcTraceRequest;
            this.packageSizeGauge = packageSizeGauge;
        }

        @Override
        public void markEnqueued() {
            enqueuedTimeNanos = System.nanoTime();
        }

        @Override
        public long getQueuedTimeNanos() {
            return System.nanoTime() - enqueuedTimeNanos;
        }

        public ExportTraceServiceRequest getGrpcTraceRequest() {
            return grpcTraceRequest;
        }

        public LongGauge getPackageSizeGauge() {
            return packageSizeGauge;
        }
    }
}
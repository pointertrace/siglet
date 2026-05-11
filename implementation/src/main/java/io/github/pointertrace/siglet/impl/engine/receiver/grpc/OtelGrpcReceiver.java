package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.metric.Metric;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.engine.SigletContext;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.SignalDestination;
import io.github.pointertrace.siglet.impl.engine.State;
import io.github.pointertrace.siglet.impl.engine.receiver.Receiver;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OtelGrpcReceiver implements Receiver {

    private final ReceiverNode receiverNode;

    private State state = State.CREATED;

    private final NettyServerBuilder serverBuilder;

    private Server server;

    private OtelGrpcMetricService metricService;

    private OtelGrpcTraceService spanService;

    private final SigletContext sigletContext;

    private final SignalCapabilities signalCapabilities = SignalCapabilities.of(Span.class, Metric.class);

    public OtelGrpcReceiver(SigletContext sigletContext, ReceiverNode receiverNode) {
        this.sigletContext = sigletContext;
        this.receiverNode = receiverNode;
        ReceiverDescriptor receiverDescriptor = receiverNode.getDescription();
        if (receiverDescriptor.getConfig() instanceof OtelGrpcReceiverConfig otelGrpcReceiverConfig) {
            serverBuilder =
                    NettyServerBuilder.forAddress(otelGrpcReceiverConfig.getAddress().getInetSocketAddress());
        } else {
            throw new SigletError("Receiver config is not of type " + OtelGrpcReceiverConfig.class.getName());
        }
    }


    public synchronized void start() {
        state = State.STARTING;
        try {
            server = serverBuilder.build();
            server.start();
        } catch (IOException e) {
            throw new SigletError("Error starting grpc server " + server + ", " + e.getMessage(), e);
        }
        state = State.RUNNING;
    }


    public synchronized void stop() {
        try {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            server.shutdownNow();
        }
    }

    public synchronized State getState() {
        return state;
    }

    @Override
    public String getName() {
        return receiverNode.getName();
    }

    @Override
    public ReceiverNode getNode() {
        return receiverNode;
    }

    @Override
    public void connect(SignalDestination destination) {
        signalCapabilities.checkCompatibility(destination.getIncomingCapabilities());
        if (destination.getIncomingCapabilities().isAbleToHandle(Span.class)) {
            if (spanService == null) {
                spanService = new OtelGrpcTraceService(sigletContext);
            }
            spanService.addDestination(destination);
            serverBuilder.addService(spanService);
        } else if (destination.getIncomingCapabilities().isAbleToHandle(Metric.class)) {
            if (metricService == null) {
                metricService = new OtelGrpcMetricService(sigletContext);
            }
            metricService.addDestination(destination);
            serverBuilder.addService(metricService);
        }
    }

    @Override
    public SignalCapabilities getOutgoingCapabilities() {
        return signalCapabilities;
    }
}

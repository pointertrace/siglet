package io.github.pointertrace.siglet.container.engine.receiver.grpc;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.config.graph.SignalType;
import io.github.pointertrace.siglet.container.config.raw.GrpcReceiverConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.SignalDestination;
import io.github.pointertrace.siglet.container.engine.State;
import io.github.pointertrace.siglet.container.engine.receiver.Receiver;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GrpcReceiver implements Receiver {

    private final ReceiverNode receiverNode;

    private final GrpcReceiverConfig grpcReceiverConfig;

    private State state = State.CREATED;

    private final NettyServerBuilder serverBuilder;

    private Server server;

    private OtelGrpcMetricService metricService;

    private OtelGrpcTraceService spanService;

    private final Context context;

    public GrpcReceiver(Context context, ReceiverNode receiverNode) {
        this.context = context;
        this.receiverNode = receiverNode;
        this.grpcReceiverConfig = (GrpcReceiverConfig) receiverNode.getConfig();

        serverBuilder = NettyServerBuilder.forAddress(grpcReceiverConfig.getAddress());
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

    public Class<? extends Signal> getSignalType(ReceiverNode node) {
        return ((GrpcReceiverConfig) node.getConfig()).getSignal().getBaseType();
    }

    @Override
    public ReceiverNode getNode() {
        return receiverNode;
    }

    @Override
    public void connect(SignalDestination destination) {
        if (destination.getSignalCapabilities().contains(SignalType.SPAN)) {
            if (spanService == null) {
                spanService = new OtelGrpcTraceService(context);
            }
            spanService.addDestination(destination);
            serverBuilder.addService(spanService);
        } else if (destination.getSignalCapabilities().contains(SignalType.METRIC)) {
            if (metricService == null) {
                metricService = new OtelGrpcMetricService(context);
            }
            metricService.addDestination(destination);
            serverBuilder.addService(metricService);
        } else {
            throw new SigletError(String.format("Cannot connect %s to %s because it does not support any of the " +
                            "following signal types: %s", destination.getName(), getName(),
                    destination.getSignalCapabilities().stream()
                            .map(SignalType::name)
                            .collect(Collectors.joining(", "))));
        }
    }
}

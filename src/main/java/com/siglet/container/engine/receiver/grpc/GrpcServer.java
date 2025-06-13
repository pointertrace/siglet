package com.siglet.container.engine.receiver.grpc;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.api.unmodifiable.metric.UnmodifiableMetric;
import com.siglet.api.unmodifiable.trace.UnmodifiableSpan;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.config.raw.GrpcReceiverConfig;
import com.siglet.container.engine.State;
import com.siglet.container.engine.receiver.Receiver;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class GrpcServer {

    NettyServerBuilder serverBuilder;

    private State state = State.CREATED;

    private final InetSocketAddress address;

    private Server server;

    public GrpcServer(InetSocketAddress address) {
        this.address = address;
        serverBuilder = NettyServerBuilder.forAddress(address);
    }


    public synchronized void start() {
        state = State.STOPPING;
        try {
            serverBuilder.build().start();
            server.start();
        } catch (IOException e) {
            throw new SigletError("Error starting grpc server " + server + ", " + e.getMessage(), e);
        }
        state = State.STOPPED;

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

    public InetSocketAddress getAddress() {
        return address;
    }

    public <T extends Signal> Receiver createReceiver(ReceiverNode node) {
        if (UnmodifiableSpan.class.isAssignableFrom(getSignalType(node))) {
            return new OtelGrpcTraceReceiver(this, node);
        }
        if (UnmodifiableMetric.class.isAssignableFrom(getSignalType(node))) {
            return new OtelGrpcMetricReceiver(this, node);
        }
        throw new SigletError("Cannot create receiver of type " + getSignalType(node) + " for name " + node.getName());
    }

    public Class<? extends Signal> getSignalType(ReceiverNode node) {
        return ((GrpcReceiverConfig) node.getConfig()).getSignal().getBaseType();
    }
}

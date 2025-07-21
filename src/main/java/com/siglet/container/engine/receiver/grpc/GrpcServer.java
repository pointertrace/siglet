package com.siglet.container.engine.receiver.grpc;

import com.siglet.SigletError;
import com.siglet.api.Signal;
import com.siglet.api.signal.trace.Span;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.config.raw.GrpcReceiverConfig;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.State;
import com.siglet.container.engine.receiver.Receiver;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class GrpcServer {


    private State state = State.CREATED;

    private final InetSocketAddress address;

    private final NettyServerBuilder serverBuilder;

    private Server server;

    public GrpcServer(InetSocketAddress address) {
//        this.address = address;

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.address = new InetSocketAddress(inetAddress, 8080);
//        this.address = new InetSocketAddress("0.0.0.0", 8080);;
//        serverBuilder = NettyServerBuilder.forAddress(address);
        serverBuilder = NettyServerBuilder.forPort(8080);
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

    public InetSocketAddress getAddress() {
        return address;
    }

    public <T extends Signal> Receiver createReceiver(Context context, ReceiverNode node) {
        if (Span.class.isAssignableFrom(getSignalType(node))) {
            return new OtelGrpcTraceReceiver(context, this, node);
        }
        throw new SigletError("Cannot create receiver of type " + getSignalType(node) + " for name " + node.getName());
    }

    public Class<? extends Signal> getSignalType(ReceiverNode node) {
        return ((GrpcReceiverConfig) node.getConfig()).getSignal().getBaseType();
    }

    public void addService(BindableService bindableService) {
        serverBuilder.addService(bindableService);
    }
}

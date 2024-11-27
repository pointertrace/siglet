package com.siglet.camel.component.otelgrpc;

import com.siglet.SigletError;
import io.grpc.BindableService;
import io.grpc.ForwardingServerBuilder;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GrpcServers {

    Map<InetSocketAddress, NettyServerBuilder> serversBuilders = new HashMap<>();

    public void addServer(InetSocketAddress address, BindableService service) {
        NettyServerBuilder builder = serversBuilders.get(address);
        if (builder == null) {
            builder = NettyServerBuilder.forAddress(address);
            serversBuilders.put(address, builder);
        }
        builder.addService(service);
    }

    public void start() {
        serversBuilders.values().stream()
                .map(ForwardingServerBuilder::build)
                .forEach(this::startServer);

    }

    private void startServer(Server server) {
        try {
            server.start();
        } catch (IOException e) {
            throw new SigletError("Error starting grpc server " + server + ", " + e.getMessage(), e);
        }
    }

    private void stopServer(Server server) {
        try {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            server.shutdownNow();
        }
    }

    public void stop() {
        serversBuilders.values().stream()
                .map(ForwardingServerBuilder::build)
                .forEach(this::stopServer);
    }


}

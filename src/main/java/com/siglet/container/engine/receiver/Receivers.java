package com.siglet.container.engine.receiver;

import com.siglet.SigletError;
import com.siglet.container.config.graph.ReceiverNode;
import com.siglet.container.config.raw.DebugReceiverConfig;
import com.siglet.container.config.raw.GrpcReceiverConfig;
import com.siglet.container.engine.receiver.debug.DebugReceiver;
import com.siglet.container.engine.receiver.grpc.GrpcServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Receivers {

    private final Map<String, Receiver> receivers = new HashMap<>();

    private final Map<InetSocketAddress, GrpcServer> servers = new HashMap<>();

    public  Receiver getReceiver(String name) {
        return receivers.get(name);
    }

    public  Receiver create(ReceiverNode node) {
        String name = node.getConfig().getName();
        if (receivers.containsKey(name)) {
            throw new SigletError("Receiver with name " + name + " already exists");
        }
        if (node.getConfig() instanceof GrpcReceiverConfig grpcConfig) {
            GrpcServer server = servers.computeIfAbsent(grpcConfig.getAddress(),
                    ignore -> new GrpcServer(grpcConfig.getAddress()));
            return receivers.put(name, server.createReceiver(node));
        } else if (node.getConfig() instanceof DebugReceiverConfig debugConfig) {
            return receivers.put(name, new DebugReceiver(node));
        } else {
            throw new SigletError(String.format("Cannot create receiver for config type %s",
                    node.getConfig().getClass().getName()));

        }

    }

    public void forEach(Consumer<Receiver> receiverConsumer) {
        receivers.values().forEach(receiverConsumer);
    }

    public void start() {
        receivers.values().forEach(Receiver::start);
    }

    public void stop() {
        receivers.values().forEach(Receiver::stop);
    }
}

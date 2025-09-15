package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.config.raw.DebugReceiverConfig;
import io.github.pointertrace.siglet.container.config.raw.GrpcReceiverConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceiver;
import io.github.pointertrace.siglet.container.engine.receiver.grpc.GrpcReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Receivers {

    private final Map<String, Receiver> receiverRegistry = new HashMap<>();

    public  Receiver getReceiver(String name) {
        return receiverRegistry.get(name);
    }

    public  Receiver create(Context context, ReceiverNode node) {
        String name = node.getConfig().getName();
        if (receiverRegistry.containsKey(name)) {
            throw new SigletError("Receiver with name " + name + " already exists");
        }
        if (node.getConfig() instanceof GrpcReceiverConfig) {
            return receiverRegistry.put(name, new GrpcReceiver(context, node));
        } else if (node.getConfig() instanceof DebugReceiverConfig) {
            return receiverRegistry.put(name, new DebugReceiver(node));
        } else {
            throw new SigletError(String.format("Cannot create receiver for config type %s",
                    node.getConfig().getClass().getName()));
        }
    }

    public void forEach(Consumer<Receiver> receiverConsumer) {
        receiverRegistry.values().forEach(receiverConsumer);
    }

    public void start() {
        receiverRegistry.values().forEach(Receiver::start);
    }

    public void stop() {
        receiverRegistry.values().forEach(Receiver::stop);
    }
}

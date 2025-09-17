package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.config.raw.GrpcReceiverConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceiver;
import io.github.pointertrace.siglet.container.engine.receiver.grpc.OtelGrpcReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Receivers {

    private final Map<String, Receiver> receiverRegistry = new HashMap<>();

    public Receiver getReceiver(String name) {
        return receiverRegistry.get(name);
    }

    public Receiver create(Context context, ReceiverNode receiverNode) {
        return receiverRegistry.put(receiverNode.getName(), context.createReceiver(receiverNode));
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

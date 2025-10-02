package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Receivers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receivers.class);

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
        LOGGER.info("Stopping receivers");
        receiverRegistry.values().forEach(Receiver::stop);
        LOGGER.info("All Receivers stoped");
    }
}

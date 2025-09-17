package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.receiver.debug.DebugReceiverType;
import io.github.pointertrace.siglet.container.engine.receiver.grpc.OtelGrpcReceiverType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReceiverTypeRegistry {

    private final Map<String, ReceiverType> definitions = new HashMap<>();

    public ReceiverTypeRegistry() {
        register(new DebugReceiverType());
        register(new OtelGrpcReceiverType());
    }

    public Set<String> getReceiverTypesNames() {
        return Collections.unmodifiableSet(definitions.keySet());
    }

    public void register(ReceiverType receiverType) {
        definitions.put(receiverType.getName(), receiverType);
    }

    public ReceiverType get(String type) {
        return definitions.get(type);
    }

    public Receiver create(Context context, ReceiverNode receiverNode) {
        ReceiverType processorType = definitions.get(receiverNode.getConfig().getType());
        if (processorType == null) {
            throw new SigletError("Receiver type " + receiverNode.getConfig().getType() + " not found");
        }
        return processorType.getReceiverCreator().create(context, receiverNode);
    }


}

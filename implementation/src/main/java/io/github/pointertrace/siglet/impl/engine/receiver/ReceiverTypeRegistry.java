package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.engine.component.config.ComponentTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.debug.DebugReceiverType;
import io.github.pointertrace.siglet.impl.engine.receiver.grpc.OtelGrpcReceiverType;


public class ReceiverTypeRegistry extends ComponentTypeRegistry<ReceiverType<?>> {

    public ReceiverTypeRegistry() {
        register(new DebugReceiverType());
        register(new OtelGrpcReceiverType());
    }
}

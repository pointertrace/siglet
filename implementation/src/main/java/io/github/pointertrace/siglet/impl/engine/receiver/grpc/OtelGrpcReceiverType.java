package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.raw.ReceiverConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverCreator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.*;

public class OtelGrpcReceiverType implements ReceiverType {

    @Override
    public String getName() {
        return "grpc";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return () -> requiredProperty(ReceiverConfig::setConfig, ReceiverConfig::setConfigLocation, "config",
                strictObject(OtelGrpcReceiverConfig::new,
                        requiredProperty(OtelGrpcReceiverConfig::setAddress, OtelGrpcReceiverConfig::setAddressLocation,
                                "address", text(inetSocketAddress()))));
    }

    @Override
    public ReceiverCreator getReceiverCreator() {
        return (context, receiverNode) -> new OtelGrpcReceiver(context, receiverNode);
    }
}

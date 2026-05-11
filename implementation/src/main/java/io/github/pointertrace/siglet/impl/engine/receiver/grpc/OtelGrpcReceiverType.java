package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.config.descriptor.InetSocketAddressTransform;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

import java.util.List;

import static io.github.pointertrace.siglet.parser.SchemaBuilder.*;

public class OtelGrpcReceiverType implements ReceiverType<OtelGrpcReceiverConfig> {

    @Override
    public String getType() {
        return "grpc";
    }

    @Override
    public ConfigurationFactory<OtelGrpcReceiverConfig> getConfigurationFactory() {
        return ConfigurationFactory.of(
                List.of(
                        property("address",OtelGrpcReceiverConfig::setAddress,string().transform(new InetSocketAddressTransform()))
                ),OtelGrpcReceiverConfig.class
        );
    }

    @Override
    public ComponentCreator<ReceiverNode> getComponentCreator() {
        return (context, receiverNode) -> new OtelGrpcReceiver(context, receiverNode);
    }
}

package io.github.pointertrace.siglet.impl.engine.receiver.debug;

import io.github.pointertrace.siglet.impl.config.graph.ReceiverNode;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverCreator;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverType;

public class DebugReceiverType implements ReceiverType<Void> {


    @Override
    public String getType() {
        return "debug";
    }

    @Override
    public ConfigurationFactory<Void> getConfigurationFactory() {
        return ConfigurationFactory.of();
    }

    @Override
    public ComponentCreator<ReceiverNode> getComponentCreator() {
        return (context, node) -> new DebugReceiver(node);
    }

}

package io.github.pointertrace.siglet.container.engine.receiver.debug;

import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverCreator;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverType;
import io.github.pointertrace.siglet.parser.schema.EmptyPropertyChecker;

public class DebugReceiverType implements ReceiverType {


    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return () -> new EmptyPropertyChecker("config");
    }

    @Override
    public ReceiverCreator getReceiverCreator() {
        return (context, node) -> new DebugReceiver(node);
    }

}

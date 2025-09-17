package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;

public interface ReceiverType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ReceiverCreator getReceiverCreator();

}

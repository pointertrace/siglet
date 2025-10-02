package io.github.pointertrace.siglet.impl.engine.receiver;

import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;

public interface ReceiverType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ReceiverCreator getReceiverCreator();

}

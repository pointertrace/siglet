package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.container.config.graph.SignalType;

public interface ProcessorType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ProcessorCreator getProcessorCreator();

    SignalType getSignalType();
}

package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.impl.config.graph.SignalType;

public interface ProcessorType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ProcessorCreator getProcessorCreator();

    SignalType getSignalType();
}

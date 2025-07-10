package com.siglet.container.engine.pipeline.processor;

public interface ProcessorType {

    String getName();

    ConfigDefinition getConfigDefinition();

    ProcessorCreator getProcessorCreator();
}

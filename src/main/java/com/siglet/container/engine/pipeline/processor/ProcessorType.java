package com.siglet.container.engine.pipeline.processor;

public class ProcessorType {

    private final String name;

    private final ConfigDefinition configDefinition;

    private final ProcessorCreator processorCreator;

    public ProcessorType(String name, ConfigDefinition configDefinition, ProcessorCreator processorCreator) {
        this.name = name;
        this.configDefinition = configDefinition;
        this.processorCreator = processorCreator;
    }

    public String getName() {
        return name;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }

    public ProcessorCreator getProcessorCreator() {
        return processorCreator;
    }

}

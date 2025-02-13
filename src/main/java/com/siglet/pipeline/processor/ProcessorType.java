package com.siglet.pipeline.processor;

import com.siglet.pipeline.processor.common.ConfigDefinition;

public class ProcessorType {

    private final ConfigDefinition configDefinition;

    private final String name;

    public ProcessorType(String name, ConfigDefinition configDefinition) {
        this.name = name;
        this.configDefinition = configDefinition;
    }

    public String getName() {
        return name;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }
}

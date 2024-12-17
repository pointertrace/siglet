package com.siglet.pipeline.spanlet.trace;

import com.siglet.pipeline.common.ConfigDefinition;

public class TraceletType {

    private final ConfigDefinition configDefinition;

    private final String name;

    public TraceletType(String name, ConfigDefinition configDefinition) {
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

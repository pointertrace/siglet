package com.siglet.pipeline.spanlet.span;

import com.siglet.pipeline.common.ConfigDefinition;

public class SpanletType {

    private final ConfigDefinition configDefinition;

    private final String name;

    public SpanletType(String name, ConfigDefinition configDefinition) {
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

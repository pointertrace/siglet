package com.siglet.spanlet.span;

import com.siglet.spanlet.ConfigDefinition;

public class SpanletType {

    private final ConfigDefinition configDefinition;

    private final Class<?> clazz;

    private final String name;

    public SpanletType(String name, Class<?> clazz, ConfigDefinition configDefinition) {
        this.name = name;
        this.clazz = clazz;
        this.configDefinition = configDefinition;
    }

    public String getName() {
        return name;
    }

    public ConfigDefinition getConfigDefinition() {
        return configDefinition;
    }
}

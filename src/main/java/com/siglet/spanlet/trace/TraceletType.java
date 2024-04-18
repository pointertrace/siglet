package com.siglet.spanlet.trace;

import com.siglet.spanlet.ConfigDefinition;

public class TraceletType {

    private final ConfigDefinition configDefinition;

    private final Class<?> clazz;

    private final String name;

    public TraceletType(String name, Class<?> clazz, ConfigDefinition configDefinition) {
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

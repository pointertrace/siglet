package com.siglet.pipeline.metriclet;

import com.siglet.pipeline.common.ConfigDefinition;

public class MetricletType {

    private final ConfigDefinition configDefinition;

    private final String name;

    public MetricletType(String name, ConfigDefinition configDefinition) {
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

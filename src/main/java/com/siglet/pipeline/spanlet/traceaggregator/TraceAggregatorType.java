package com.siglet.pipeline.spanlet.traceaggregator;

import com.siglet.pipeline.common.ConfigDefinition;

public class TraceAggregatorType {

    private final ConfigDefinition configDefinition;

    private final String name;

    public TraceAggregatorType(String name, ConfigDefinition configDefinition) {
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

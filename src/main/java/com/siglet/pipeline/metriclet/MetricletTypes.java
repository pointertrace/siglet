package com.siglet.pipeline.metriclet;

import com.siglet.pipeline.common.filter.FilterDefinition;
import com.siglet.pipeline.common.processor.ProcessorDefinition;
import com.siglet.pipeline.common.router.RouterDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetricletTypes {

    private final Map<String, MetricletType> definitions = new HashMap<>();

    public void add(MetricletType spanletType) {
        definitions.put(spanletType.getName(), spanletType);
    }

    public MetricletTypes() {
        add(new MetricletType("processor", null, new ProcessorDefinition()));
        add(new MetricletType("filter", null, new FilterDefinition()));
        add(new MetricletType("router", null, new RouterDefinition()));
    }

    public MetricletType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

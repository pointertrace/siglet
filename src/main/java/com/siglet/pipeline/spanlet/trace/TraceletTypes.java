package com.siglet.pipeline.spanlet.trace;

import com.siglet.pipeline.common.filter.FilterDefinition;
import com.siglet.pipeline.common.processor.ProcessorDefinition;
import com.siglet.pipeline.common.router.RouterDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TraceletTypes {

    private final Map<String, TraceletType> definitions = new HashMap<>();

    public void add(TraceletType traceletType) {
        definitions.put(traceletType.getName(), traceletType);
    }

    public TraceletTypes() {
        add(new TraceletType("processor", new ProcessorDefinition()));
        add(new TraceletType("filter", new FilterDefinition()));
        add(new TraceletType("router", new RouterDefinition()));
    }

    public TraceletType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

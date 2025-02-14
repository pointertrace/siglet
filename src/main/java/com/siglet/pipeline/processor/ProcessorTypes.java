package com.siglet.pipeline.processor;

import com.siglet.pipeline.processor.common.action.ActionDefinition;
import com.siglet.pipeline.processor.common.filter.FilterDefinition;
import com.siglet.pipeline.processor.common.router.RouterDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProcessorTypes {

    private final Map<String, ProcessorType> definitions = new HashMap<>();

    public void add(ProcessorType processorType) {
        definitions.put(processorType.getName(), processorType);
    }

    public ProcessorTypes() {
        add(new ProcessorType("groovy-action", new ActionDefinition()));
        add(new ProcessorType("groovy-filter", new FilterDefinition()));
        add(new ProcessorType("groovy-router", new RouterDefinition()));
    }

    public ProcessorType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

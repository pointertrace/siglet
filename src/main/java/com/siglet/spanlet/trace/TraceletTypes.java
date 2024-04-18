package com.siglet.spanlet.trace;

import com.siglet.spanlet.filter.FilterDefinition;
import com.siglet.spanlet.processor.ProcessorDefinition;
import com.siglet.spanlet.router.RouterDefinition;
import com.siglet.spanlet.span.SpanletType;

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
        add(new TraceletType("processor", null, new ProcessorDefinition()));
        add(new TraceletType("filter", null, new FilterDefinition()));
        add(new TraceletType("router", null, new RouterDefinition()));
    }

    public TraceletType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

package com.siglet.pipeline.spanlet.span;

import com.siglet.pipeline.common.filter.FilterDefinition;
import com.siglet.pipeline.common.processor.ProcessorDefinition;
import com.siglet.pipeline.common.router.RouterDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpanletTypes {

    private final Map<String, SpanletType> definitions = new HashMap<>();

    public void add(SpanletType spanletType) {
        definitions.put(spanletType.getName(), spanletType);
    }

    public SpanletTypes() {
        add(new SpanletType("processor", null, new ProcessorDefinition()));
        add(new SpanletType("filter", null, new FilterDefinition()));
        add(new SpanletType("router", null, new RouterDefinition()));
    }

    public SpanletType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

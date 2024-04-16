package com.siglet.spanlet.traceaggregator;

import com.siglet.spanlet.SpanletType;
import com.siglet.spanlet.filter.FilterDefinition;
import com.siglet.spanlet.processor.ProcessorDefinition;
import com.siglet.spanlet.router.RouterDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TraceAggregatorTypes {

    private final Map<String, TraceAggregatorType> definitions = new HashMap<>();

    public void add(TraceAggregatorType traceAggregatorType) {
        definitions.put(traceAggregatorType.getName(), traceAggregatorType);
    }

    public TraceAggregatorTypes() {
        add(new TraceAggregatorType("default", null, new TraceAggregationDefinition()));
    }

    public TraceAggregatorType get(String type) {
        return definitions.get(type);
    }

    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(definitions.keySet());

    }


}

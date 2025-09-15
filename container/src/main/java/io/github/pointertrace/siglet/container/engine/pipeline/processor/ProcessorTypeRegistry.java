package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action.SpanletGroovyActionProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter.SpanletGroovyFilterProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router.SpanletGroovyRouterProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.siglet.SigletProcessorType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProcessorTypeRegistry {

    private final Map<String, ProcessorType> definitions = new HashMap<>();

    public ProcessorTypeRegistry() {
        register(new SpanletGroovyActionProcessorType());
        register(new SpanletGroovyFilterProcessorType());
        register(new SpanletGroovyRouterProcessorType());
    }

    public Set<String> getProcessorTypesNames() {
        return Collections.unmodifiableSet(definitions.keySet());
    }

    public void register(ProcessorType processorType) {
        definitions.put(processorType.getName(), processorType);
    }

    public ProcessorType get(String type) {
        return definitions.get(type);
    }

    public void register(SigletBundle sigletBundle) {
        sigletBundle.definitions().forEach(sd -> register(new SigletProcessorType(sd)));
        ;
    }

    public Processor create(Context context, ProcessorNode node) {
        ProcessorType processorType = definitions.get(node.getConfig().getType());
        if (processorType == null) {
            throw new SigletError("Processor type " + node.getConfig().getType() + " not found");
        }
        return processorType.getProcessorCreator().create(context, node);
    }


}

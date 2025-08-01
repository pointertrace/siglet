package io.github.pointertrace.siglet.container.engine.pipeline.processor;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.engine.Context;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.filter.GroovyFilterProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.router.GroovyRouterProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.siglet.SigletProcessorType;

import java.util.HashMap;
import java.util.Map;

public class ProcessorTypeRegistry {

    private final Map<String, ProcessorType> definitions = new HashMap<>();

    public ProcessorTypeRegistry() {
        register(new GroovyActionProcessorType());
        register(new GroovyFilterProcessorType());
        register(new GroovyRouterProcessorType());
    }

    public void register(ProcessorType processorType) {
        definitions.put(processorType.getName(), processorType);
    }

    public ProcessorType get(String type) {
        return definitions.get(type);
    }

    public void register(SigletConfig sigletConfig) {
        register(new SigletProcessorType(sigletConfig));
    }

    public Processor create(Context context, ProcessorNode node) {
        ProcessorType processorType = definitions.get(node.getConfig().getType());
        if (processorType == null) {
            throw new SigletError("Processor type " + node.getConfig().getType() + " not found");
        }
        return processorType.getProcessorCreator().create(context, node);
    }


}

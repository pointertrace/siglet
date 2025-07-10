package com.siglet.container.engine.pipeline.processor;

import com.siglet.SigletError;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionProcessorType;
import com.siglet.container.engine.pipeline.processor.groovy.filter.GroovyFilterProcessorType;
import com.siglet.container.engine.pipeline.processor.groovy.router.GroovyRouterProcessorType;
import com.siglet.container.engine.pipeline.processor.siglet.SigletProcessorType;

import java.util.HashMap;
import java.util.Map;

public class ProcessorTypes {

    private final Map<String, ProcessorType> definitions = new HashMap<>();

    public ProcessorTypes() {
        add(new GroovyActionProcessorType());
        add(new GroovyFilterProcessorType());
        add(new GroovyRouterProcessorType());
    }

    public void add(ProcessorType processorType) {
        definitions.put(processorType.getName(), processorType);
    }

    public ProcessorType get(String type) {
        return definitions.get(type);
    }

    public void add(SigletConfig sigletConfig) {
        add(new SigletProcessorType(sigletConfig));
    }

    public Processor create(Context context, ProcessorNode node) {
        ProcessorType processorType = definitions.get(node.getConfig().getType());
        if (processorType == null) {
            throw new SigletError("Processor type " + node.getConfig().getType() + " not found");
        }
        return processorType.getProcessorCreator().create(context, node);
    }


}

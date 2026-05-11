package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;

public class SpanletGroovyActionProcessorType extends GroovyActionProcessorType {

    @Override
    public String getType() {
        return "spanlet-groovy-action";
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (context, processorNode) -> new GroovyActionProcessor(context, processorNode);
    }

}

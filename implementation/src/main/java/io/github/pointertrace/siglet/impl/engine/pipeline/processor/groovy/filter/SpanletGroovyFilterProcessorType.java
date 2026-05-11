package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.filter;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;

public class SpanletGroovyFilterProcessorType extends GroovyFilterProcessorType {

    @Override
    public String getType() {
        return "spanlet-groovy-filter";
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (context, processorNode) -> new GroovyFilterProcessor(context, processorNode);
    }

}

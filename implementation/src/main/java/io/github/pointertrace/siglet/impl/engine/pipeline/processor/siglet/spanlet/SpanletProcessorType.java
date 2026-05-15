package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet;

import io.github.pointertrace.siglet.impl.config.graph.ProcessorNode;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.ComponentCreator;
import io.github.pointertrace.siglet.impl.engine.ConfigurationFactory;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;

public class SpanletProcessorType<T> implements ProcessorType<T> {

    private final SigletDefinition sigletDefinition;

    public SpanletProcessorType(SigletDefinition sigletDefinition) {
        this.sigletDefinition = sigletDefinition;
    }

    @Override
    public String getType() {
       return sigletDefinition.getName();
    }

    @Override
    public ConfigurationFactory<T> getConfigurationFactory() {
        return (ConfigurationFactory<T>) sigletDefinition.createConfigurationFactory();
    }

    @Override
    public ComponentCreator<ProcessorNode> getComponentCreator() {
        return (sigletContext, node) -> new SpanletProcessor(sigletContext, node,sigletDefinition.createProcessor());
    }
}

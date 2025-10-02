package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.api.Siglet;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.impl.config.graph.SignalType;
import io.github.pointertrace.siglet.impl.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.impl.config.siglet.SigletDefinition;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorCreator;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet.SpanletProcessor;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.schema.EmptyPropertyChecker;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.requiredProperty;


public class SigletProcessorType implements ProcessorType {

    private final SigletDefinition sigletDefinition;

    public SigletProcessorType(SigletDefinition sigletDefinition) {
        this.sigletDefinition = sigletDefinition;
    }


    @Override
    public String getName() {
        return sigletDefinition.getSigletConfig().name();
    }

    @Override
    public ConfigDefinition getConfigDefinition() {


        NodeChecker nodeChecker = sigletDefinition.createConfigChecker();
        if (nodeChecker == null) {
            return () -> new EmptyPropertyChecker("config");
        } else {
            return () -> requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                    nodeChecker);
        }
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return (context, node) -> {
            Siglet instance = sigletDefinition.createProcessor();
            if (instance instanceof Spanlet spanlet) {
                return new SpanletProcessor(context, node, spanlet);
            }
            throw new SigletError(String.format("Cannot create siglet type %s", node.getName()));
        };
    }

    @Override
    public SignalType getSignalType() {
        return sigletDefinition.getSignalType();
    }

}

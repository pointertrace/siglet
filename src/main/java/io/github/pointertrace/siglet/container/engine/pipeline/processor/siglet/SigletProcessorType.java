package io.github.pointertrace.siglet.container.engine.pipeline.processor.siglet;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siget.parser.schema.EmptyPropertyChecker;
import io.github.pointertrace.siglet.api.signal.trace.Spanlet;
import io.github.pointertrace.siglet.container.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.container.config.raw.ProcessorKind;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ConfigDefinition;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorCreator;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.siglet.spanlet.SpanletProcessor;

import static io.github.pointertrace.siget.parser.schema.SchemaFactory.requiredProperty;


public class SigletProcessorType implements ProcessorType {

    private final SigletConfig sigletConfig;

    public SigletProcessorType(SigletConfig sigletConfig) {
        this.sigletConfig = sigletConfig;
    }


    @Override
    public String getName() {
        return sigletConfig.name();
    }

    @Override
    public ConfigDefinition getConfigDefinition() {
        return createConfigDefinition(sigletConfig);
    }

    @Override
    public ProcessorCreator getProcessorCreator() {
        return (context, node) -> {
            if (node.getConfig().getProcessorKind() == ProcessorKind.SPANLET) {
                io.github.pointertrace.siglet.api.Processor instance = sigletConfig.createSigletInstance();
                if (instance instanceof Spanlet spanlet) {
                    return new SpanletProcessor(context, node, spanlet);
                }
            }
            throw new SigletError(String.format("Cannot create siglet type %s", node.getName()));
        };
    }

    private ConfigDefinition createConfigDefinition(SigletConfig sigletConfig) {
        if (sigletConfig.configChecker() == null) {
            return () -> new EmptyPropertyChecker("config");
        } else {
            return () -> requiredProperty(ProcessorConfig::setConfig, ProcessorConfig::setConfigLocation, "config",
                    sigletConfig.configChecker());
        }
    }
}

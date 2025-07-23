package com.siglet.container.engine.pipeline.processor.siglet;

import com.siglet.SigletError;
import com.siglet.api.signal.trace.Spanlet;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.pipeline.processor.ConfigDefinition;
import com.siglet.container.engine.pipeline.processor.ProcessorCreator;
import com.siglet.container.engine.pipeline.processor.ProcessorType;
import com.siglet.container.engine.pipeline.processor.siglet.spanlet.SpanletProcessor;
import com.siglet.parser.schema.EmptyPropertyChecker;

import static com.siglet.parser.schema.SchemaFactory.requiredProperty;

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
                com.siglet.api.Processor instance = sigletConfig.createSigletInstance();
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

package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.validator.ComposedValidator;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.spanlet.SpanletProcessorType;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;

import java.util.List;


public class ConfigFactory {

    private static final ComposedValidator composedValidator = new ComposedValidator();

    public Config create(String yaml) {
        return create(yaml, List.of());
    }

    public Config create(String yaml, List<SigletBundle> sigletBundles) {

        ReceiverTypeRegistry receiverTypeRegistry = new ReceiverTypeRegistry();

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        ExporterTypeRegistry exporterTypeRegistry = new ExporterTypeRegistry();

        processorTypeRegistry.registerAll(
                sigletBundles.stream()
                        .flatMap(sb -> sb.getDefinitions().stream())
                        .<ProcessorType<?>>map(sd -> new SpanletProcessorType(sd)).toList());

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml, receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry);

        composedValidator.validate(yamlDescriptor);

        return new Config(yamlDescriptor, receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry);
    }

}

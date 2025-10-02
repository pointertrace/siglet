package io.github.pointertrace.siglet.impl.config;

import io.github.pointertrace.siglet.impl.config.raw.RawConfig;
import io.github.pointertrace.siglet.impl.config.raw.validator.ComposedValidator;
import io.github.pointertrace.siglet.impl.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;

import java.util.List;

import static io.github.pointertrace.siglet.impl.config.ConfigCheckFactory.rawConfigChecker;

public class ConfigFactory {

    private static final ComposedValidator composedValidator = new ComposedValidator();

    public Config create(String yaml) {
        return create(yaml, List.of());
    }

    public RawConfig createRawConfig(String yaml, ReceiverTypeRegistry receiverTypeRegistry,
                                     ProcessorTypeRegistry processorTypeRegistry,
                                     ExporterTypeRegistry exporterTypeRegistry) {

        YamlParser yamlParser = new YamlParser();

        Node node = yamlParser.parse(yaml);

        rawConfigChecker(receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry).check(node);

        RawConfig rawConfig = node.getValue(RawConfig.class);
        rawConfig.afterSetValues();
        rawConfig.setSignalType(processorTypeRegistry);

        return rawConfig;
    }

    public Config create(String yaml, List<SigletBundle> sigletBundles) {

        ReceiverTypeRegistry receiverTypeRegistry = new ReceiverTypeRegistry();

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        ExporterTypeRegistry exporterTypeRegistry = new ExporterTypeRegistry();


        sigletBundles.forEach(processorTypeRegistry::register);

        RawConfig rawConfig = createRawConfig(yaml, receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry);

        composedValidator.validate(rawConfig);

        return new Config(createRawConfig(yaml, receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry),
                sigletBundles, receiverTypeRegistry, processorTypeRegistry, exporterTypeRegistry);
    }

}

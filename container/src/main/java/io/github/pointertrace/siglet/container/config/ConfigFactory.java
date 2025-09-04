package io.github.pointertrace.siglet.container.config;

import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.raw.validator.ComposedValidator;
import io.github.pointertrace.siglet.container.config.siglet.SigletBundle;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;

import java.util.List;

import static io.github.pointertrace.siglet.container.config.ConfigCheckFactory.rawConfigChecker;

public class ConfigFactory {

    private static final ComposedValidator composedValidator = new ComposedValidator();

    public Config create(String yaml) {
        return create(yaml, List.of());
    }

    public RawConfig createRawConfig(String yaml, ProcessorTypeRegistry processorTypeRegistry) {

        YamlParser yamlParser = new YamlParser();

        Node node = yamlParser.parse(yaml);

        rawConfigChecker(processorTypeRegistry).check(node);

        RawConfig rawConfig = node.getValue(RawConfig.class);
        rawConfig.afterSetValues();
        rawConfig.setSignalType(processorTypeRegistry);

        return rawConfig;
    }

    public Config create(String yaml, List<SigletBundle> sigletBundles) {

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        sigletBundles.forEach(processorTypeRegistry::register);

        RawConfig rawConfig = createRawConfig(yaml,processorTypeRegistry);

        composedValidator.validate(rawConfig);

        return new Config(createRawConfig(yaml,processorTypeRegistry), sigletBundles, processorTypeRegistry);
    }

}

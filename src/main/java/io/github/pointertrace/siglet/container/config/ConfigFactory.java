package io.github.pointertrace.siglet.container.config;

import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siget.parser.Node;
import io.github.pointertrace.siget.parser.YamlParser;

import java.util.List;

import static io.github.pointertrace.siglet.container.config.ConfigCheckFactory.rawConfigChecker;

public class ConfigFactory {

    public Config create(String yaml) {
        return create(yaml, List.of());
    }

    public Config create(String yaml, List<SigletConfig> sigletsConfigs) {

        ProcessorTypeRegistry processorTypeRegistry = new ProcessorTypeRegistry();

        sigletsConfigs.forEach(processorTypeRegistry::register);

        YamlParser yamlParser = new YamlParser();

        Node node = yamlParser.parse(yaml);

        rawConfigChecker(processorTypeRegistry).check(node);

        RawConfig rawConfig = node.getValue(RawConfig.class);
        rawConfig.afterSetValues();

        return new Config(rawConfig, sigletsConfigs, processorTypeRegistry);
    }
}

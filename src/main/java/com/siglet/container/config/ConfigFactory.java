package com.siglet.container.config;

import com.siglet.container.config.raw.RawConfig;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import com.siglet.parser.Node;
import com.siglet.parser.YamlParser;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.rawConfigChecker;

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

        return new Config(rawConfig,sigletsConfigs, processorTypeRegistry);

    }
}

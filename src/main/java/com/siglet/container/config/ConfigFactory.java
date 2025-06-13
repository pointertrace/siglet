package com.siglet.container.config;

import com.siglet.api.parser.Node;
import com.siglet.container.config.raw.RawConfig;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.pipeline.processor.ProcessorTypes;
import com.siglet.parser.YamlParser;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.globalConfigChecker;

public class ConfigFactory {

    public Config create(String yaml) {
        return create(yaml, List.of());
    }

    public Config create(String yaml, List<SigletConfig> extraSigletConfigs) {

        extraSigletConfigs.forEach(ProcessorTypes.getInstance()::add);

        YamlParser yamlParser = new YamlParser();

        Node node = yamlParser.parse(yaml);

        globalConfigChecker().check(node);

        RawConfig rawConfig = node.getValue(RawConfig.class);
        rawConfig.afterSetValues();

        return new Config(rawConfig);

    }
}

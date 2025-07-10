package com.siglet.container.config;

import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.raw.GlobalConfig;
import com.siglet.container.config.raw.RawConfig;
import com.siglet.container.config.siglet.SigletConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, SigletConfig> sigletConfigs;
    private final RawConfig rawConfig;
    private final GlobalConfig globalConfig;

    public Config(RawConfig rawConfig) {
        this(rawConfig, List.of());
    }

    public Config(RawConfig rawConfig, List<SigletConfig> extraSigletConfigs) {
        this.rawConfig = rawConfig;
        this.sigletConfigs = extraSigletConfigs.stream()
                .collect(Collectors.toMap(SigletConfig::name, Function.identity()));
        this.globalConfig = rawConfig.getGlobalConfig() == null ? new GlobalConfig() : rawConfig.getGlobalConfig();
    }

    public SigletConfig getSigletConfig(String name) {
        return sigletConfigs.get(name);
    }

    public Graph getGraph() {
        return  rawConfig.createGraph();
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}

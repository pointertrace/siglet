package io.github.pointertrace.siglet.container.config;

import io.github.pointertrace.siglet.container.config.graph.Graph;
import io.github.pointertrace.siglet.container.config.graph.GraphFactory;
import io.github.pointertrace.siglet.container.config.raw.GlobalConfig;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.siglet.SigletConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, SigletConfig> sigletConfigs;
    private final RawConfig rawConfig;
    private Graph graph;
    private final GraphFactory graphFactory = new GraphFactory();
    private final GlobalConfig globalConfig;
    private final ProcessorTypeRegistry processorTypeRegistry;

    public Config(RawConfig rawConfig, List<SigletConfig> sigletsConfigs, ProcessorTypeRegistry processorTypeRegistry) {
        this.rawConfig = rawConfig;
        this.sigletConfigs = sigletsConfigs.stream()
                .collect(Collectors.toMap(SigletConfig::name, Function.identity()));
        this.processorTypeRegistry = processorTypeRegistry;
        this.globalConfig = rawConfig.getGlobalConfig() == null ? new GlobalConfig() : rawConfig.getGlobalConfig();
    }

    public SigletConfig getSigletConfig(String name) {
        return sigletConfigs.get(name);
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public ProcessorTypeRegistry getProcessorTypes() {
        return processorTypeRegistry;
    }

    public RawConfig getRawConfig() {
        return rawConfig;
    }

}

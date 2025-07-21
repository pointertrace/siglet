package com.siglet.container.config;

import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.raw.GlobalConfig;
import com.siglet.container.config.raw.RawConfig;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, SigletConfig> sigletConfigs;
    private final RawConfig rawConfig;
    private Graph graph;
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

    public Graph getGraph(Context context) {
        if (graph == null) {
           graph = rawConfig.createGraph(context);
        }
        return graph;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public ProcessorTypeRegistry getProcessorTypes() {
        return processorTypeRegistry;
    }

}

package com.siglet.container.config;

import com.siglet.container.config.graph.Graph;
import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.GlobalConfig;
import com.siglet.container.config.raw.RawConfig;
import com.siglet.container.config.siglet.SigletConfig;
import com.siglet.container.engine.Context;
import com.siglet.container.engine.pipeline.processor.Processor;
import com.siglet.container.engine.pipeline.processor.ProcessorTypes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {

    private final Map<String, SigletConfig> sigletConfigs;
    private final RawConfig rawConfig;
    private final GlobalConfig globalConfig;
    private final ProcessorTypes processorTypes;

    public Config(RawConfig rawConfig, List<SigletConfig> sigletsConfigs, ProcessorTypes processorTypes) {
        this.rawConfig = rawConfig;
        this.sigletConfigs = sigletsConfigs.stream()
                .collect(Collectors.toMap(SigletConfig::name, Function.identity()));
        this.processorTypes = processorTypes;
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

    public ProcessorTypes getProcessorTypes() {
        return processorTypes;
    }

}

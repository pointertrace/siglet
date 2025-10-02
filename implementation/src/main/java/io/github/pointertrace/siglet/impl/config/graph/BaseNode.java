package io.github.pointertrace.siglet.impl.config.graph;


import io.github.pointertrace.siglet.impl.config.raw.BaseConfig;

public abstract sealed class BaseNode permits ReceiverNode, PipelineNode, ProcessorNode, ExporterNode {


    private final BaseConfig config;

    protected BaseNode(BaseConfig config) {
        this.config = config;
    }

    public String getName() {
        return config.getName();
    }


    public BaseConfig getConfig() {
        return config;
    }
}

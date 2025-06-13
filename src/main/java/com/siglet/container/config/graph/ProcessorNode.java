package com.siglet.container.config.graph;

import com.siglet.container.config.raw.ProcessorConfig;

import java.util.ArrayList;
import java.util.List;

public final class ProcessorNode extends BaseNode {

    private List<BaseNode> to = new ArrayList<>();

    private PipelineNode pipeline;

    ProcessorNode(ProcessorConfig item) {
        super(item);
    }

    public List<BaseNode> getTo() {
        return to;
    }

    public void setTo(List<BaseNode> to) {
        this.to = to;
    }

    public PipelineNode getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineNode pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public ProcessorConfig getConfig() {
        return (ProcessorConfig) super.getConfig();
    }
}

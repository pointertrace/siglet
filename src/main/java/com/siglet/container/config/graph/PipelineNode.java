package com.siglet.container.config.graph;

import com.siglet.container.config.raw.PipelineConfig;

import java.util.ArrayList;
import java.util.List;

public final class PipelineNode extends BaseNode {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<ProcessorNode> start = new ArrayList<>();

    private List<ProcessorNode> siglets = new ArrayList<>();

    public PipelineNode(PipelineConfig pipelineConfig) {
        super(pipelineConfig);
    }

    public List<ProcessorNode> getSiglets() {
        return siglets;
    }

    public void setSiglets(List<ProcessorNode> siglets) {
        this.siglets = siglets;
    }

    public List<ReceiverNode> getFrom() {
        return from;
    }

    public void setFrom(List<ReceiverNode> from) {
        this.from = from;
    }

    public List<ProcessorNode> getStart() {
        return start;
    }

    public void setStart(List<ProcessorNode> start) {
        this.start = start;
    }

    @Override
    public PipelineConfig getConfig() {
        return (PipelineConfig) super.getConfig();
    }
}

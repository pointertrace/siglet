package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.impl.config.descriptor.PipelineDescriptor;

import java.util.ArrayList;
import java.util.List;

public final class PipelineNode extends BaseNode {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<ProcessorNode> start = new ArrayList<>();

    public PipelineNode(PipelineDescriptor pipelineDescriptorConfig) {
        super(pipelineDescriptorConfig);
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
    public PipelineDescriptor getDescription() {
        return (PipelineDescriptor) super.getDescription();
    }

}

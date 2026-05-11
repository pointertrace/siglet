package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;

import java.util.ArrayList;
import java.util.List;

public final class ReceiverNode extends BaseNode {

    private List<PipelineNode> to = new ArrayList<>();

    public ReceiverNode(ReceiverDescriptor item) {
        super(item);
    }

    public List<PipelineNode> getTo() {
        return to;
    }

    public void setTo(List<PipelineNode> to) {
        this.to = to;
    }

    @Override
    public ReceiverDescriptor getDescription() {
        return (ReceiverDescriptor) super.getDescription();
    }

}

package io.github.pointertrace.siglet.container.config.graph;

import io.github.pointertrace.siglet.container.config.raw.ReceiverConfig;

import java.util.ArrayList;
import java.util.List;

public final class ReceiverNode extends BaseNode {

    private List<PipelineNode> to = new ArrayList<>();

    public ReceiverNode(ReceiverConfig item) {
        super(item);
    }

    public List<PipelineNode> getTo() {
        return to;
    }

    public void setTo(List<PipelineNode> to) {
        this.to = to;
    }

    @Override
    public ReceiverConfig getConfig() {
        return (ReceiverConfig) super.getConfig();
    }

}

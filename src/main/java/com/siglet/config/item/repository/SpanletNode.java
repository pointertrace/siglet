package com.siglet.config.item.repository;

import com.siglet.config.item.SpanletItem;

import java.util.ArrayList;
import java.util.List;

public class SpanletNode extends Node<SpanletItem> {

    private List<Node<?>> to = new ArrayList<>();

    private PipelineNode pipeline;

    public SpanletNode(String name, SpanletItem item) {
        super(name, item);
    }


    public List<Node<?>> getTo() {
        return to;
    }

    public void setTo(List<Node<?>> to) {
        this.to = to;
    }

    public PipelineNode getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineNode pipeline) {
        this.pipeline = pipeline;
    }
}

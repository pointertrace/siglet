package com.siglet.config.item.repository;

import com.siglet.config.item.ProcessorItem;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorNode<T extends ProcessorItem> extends Node<T> {

    private List<Node<?>> to = new ArrayList<>();
    private PipelineNode pipeline;

    public ProcessorNode(String name, T item) {
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

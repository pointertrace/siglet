package com.siglet.config.item.repository;

import com.siglet.config.item.TracePipelineItem;

import java.util.ArrayList;
import java.util.List;

public class PipelineNode extends Node<TracePipelineItem> {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<ProcessorNode> start = new ArrayList<>();

    private List<ProcessorNode<?>> processors = new ArrayList<>();

    public PipelineNode(String name, TracePipelineItem tracePipelineItem) {
        super(name, tracePipelineItem);
    }

    public List<ProcessorNode<?>> getProcessors() {
        return processors;
    }

    public void setProcessors(List<ProcessorNode<?>> processors) {
        this.processors = processors;
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
}

package com.siglet.config.item.repository;

import com.siglet.config.item.Item;
import com.siglet.config.item.TracePipelineItem;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.ArrayList;
import java.util.List;

public class PipelineNode extends Node<TracePipelineItem> {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<SpanletNode> start = new ArrayList<>();

    private List<SpanletNode> processors = new ArrayList<>();

    public PipelineNode(String name, TracePipelineItem tracePipelineItem) {
        super(name, tracePipelineItem);
    }

    public List<SpanletNode> getProcessors() {
        return processors;
    }

    public void setProcessors(List<SpanletNode> processors) {
        this.processors = processors;
    }

    public List<ReceiverNode> getFrom() {
        return from;
    }

    public void setFrom(List<ReceiverNode> from) {
        this.from = from;
    }

    public List<SpanletNode> getStart() {
        return start;
    }

    public void setStart(List<SpanletNode> start) {
        this.start = start;
    }
}

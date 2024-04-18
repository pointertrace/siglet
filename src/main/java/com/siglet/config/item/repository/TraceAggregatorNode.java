package com.siglet.config.item.repository;

import com.siglet.spanlet.traceaggregator.TraceAggregatorItem;

import java.util.ArrayList;
import java.util.List;

public class TraceAggregatorNode extends ProcessorNode<TraceAggregatorItem> {

    private List<Node<?>> to = new ArrayList<>();

    private PipelineNode pipeline;

    public TraceAggregatorNode(String name, TraceAggregatorItem item) {
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

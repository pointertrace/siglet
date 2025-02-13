package com.siglet.config.item.repository;

import com.siglet.config.item.SigletItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public class SigletNode extends Node<SigletItem> {

    private List<Node<?>> to = new ArrayList<>();
    private PipelineNode pipeline;

    protected SigletNode(String name, SigletItem item) {
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

    @Override
    public void createRoute(RouteCreator routeCreator) {

    }
}

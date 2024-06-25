package com.siglet.config.item.repository;

import com.siglet.config.item.PipelineItem;
import com.siglet.config.item.TracePipelineItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public class PipelineNode extends Node<PipelineItem<?>> {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<ProcessorNode<?>> start = new ArrayList<>();

    private List<ProcessorNode<?>> processors = new ArrayList<>();

    public PipelineNode(String name, PipelineItem<?> pipelineItem) {
        super(name, pipelineItem);
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        if (getStart().size() == 1) {
            getStart().getFirst().createRoute(routeCreator);
        } else {
            RouteCreator multicast = routeCreator.startMulticast();
            for (ProcessorNode<?> node : getStart()) {
                node.createRoute(multicast);
            }
            multicast.endMulticast();
        }
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

    public List<ProcessorNode<?>> getStart() {
        return start;
    }

    public void setStart(List<ProcessorNode<?>> start) {
        this.start = start;
    }
}

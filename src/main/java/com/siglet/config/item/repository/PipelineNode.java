package com.siglet.config.item.repository;

import com.siglet.config.item.PipelineItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public final class PipelineNode extends Node<PipelineItem> {

    private List<ReceiverNode> from = new ArrayList<>();

    private List<SigletNode> start = new ArrayList<>();

    private List<SigletNode> siglets = new ArrayList<>();

    public PipelineNode(PipelineItem pipelineItem, NodeRepository nodeRepository) {
        super(pipelineItem, nodeRepository);
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        if (getStart().size() == 1) {
            getStart().getFirst().createRoute(routeCreator);
        } else {
            RouteCreator multicast = routeCreator.startMulticast();
            for (SigletNode node : getStart()) {
                node.createRoute(multicast);
            }
            multicast.endMulticast();
        }
    }

    public List<SigletNode> getSiglets() {
        return siglets;
    }

    public void setSiglets(List<SigletNode> siglets) {
        this.siglets = siglets;
    }

    public List<ReceiverNode> getFrom() {
        return from;
    }

    public void setFrom(List<ReceiverNode> from) {
        this.from = from;
    }

    public List<SigletNode> getStart() {
        return start;
    }

    public void setStart(List<SigletNode> start) {
        this.start = start;
    }
}

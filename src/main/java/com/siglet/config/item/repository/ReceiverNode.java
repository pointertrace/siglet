package com.siglet.config.item.repository;

import com.siglet.config.item.ReceiverItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public final class ReceiverNode extends Node<ReceiverItem> {

    private List<PipelineNode> to = new ArrayList<>();

    public ReceiverNode(ReceiverItem item, NodeRepository nodeRepository) {
        super(item, nodeRepository);
    }

    public List<PipelineNode> getTo() {
        return to;
    }

    public void setTo(List<PipelineNode> to) {
        this.to = to;
    }

    public String getUri() {
        return  getItem().getUri();
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        if (getTo().size() == 1) {
            getTo().getFirst().createRoute(routeCreator.addReceiver(getUri(),getName()));
        } else if (getTo().size() > 1) {
            throw new IllegalStateException("not yet implemented!");
        }
    }
}

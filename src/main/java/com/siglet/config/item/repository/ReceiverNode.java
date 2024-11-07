package com.siglet.config.item.repository;

import com.siglet.config.item.ReceiverItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public class ReceiverNode extends Node<ReceiverItem> {

    private List<Node<?>> to = new ArrayList<>();

    public ReceiverNode(String name, ReceiverItem item) {
        super(name, item);
    }

    public List<Node<?>> getTo() {
        return to;
    }

    public void setTo(List<Node<?>> to) {
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

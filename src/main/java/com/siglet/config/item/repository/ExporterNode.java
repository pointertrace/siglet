package com.siglet.config.item.repository;

import com.siglet.config.item.ExporterItem;
import com.siglet.config.item.repository.routecreator.RouteCreator;

import java.util.ArrayList;
import java.util.List;

public final class ExporterNode extends Node<ExporterItem> {

    private List<Node<?>> from = new ArrayList<>();

    public ExporterNode(ExporterItem exporterItem, NodeRepository nodeRepository) {
        super(exporterItem, nodeRepository);
    }

    @Override
    public void createRoute(RouteCreator routeCreator) {
        routeCreator.addExporter(getUri());
    }

    public List<Node<?>> getFrom() {
        return from;
    }

    public void setFrom(List<Node<?>> from) {
        this.from = from;
    }

    public String getUri() {
        return getItem().getUri();
    }
}

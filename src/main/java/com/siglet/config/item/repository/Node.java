package com.siglet.config.item.repository;

import com.siglet.config.item.Item;
import com.siglet.config.item.repository.routecreator.RouteCreator;

public abstract sealed class Node<T extends Item> permits ReceiverNode, PipelineNode, SigletNode, ExporterNode {


    private final T item;

    private final NodeRepository nodeRepository;

    protected Node(T item, NodeRepository nodeRepository) {
        this.item = item;
        this.nodeRepository = nodeRepository;
    }

    public String getName() {
        return item.getName();
    }


    public T getItem() {
        return item;
    }

    public NodeRepository getNodeRepository() {
        return nodeRepository;
    }

    public abstract void createRoute(RouteCreator routeCreator);

}

package com.siglet.config.item.repository;

import com.siglet.config.item.Item;
import com.siglet.config.item.repository.routecreator.RouteCreator;

public abstract class Node<T extends Item> {


    private final String name;

    private final T item;

    protected Node(String name, T item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }


    public T getItem() {
        return item;
    }

    public abstract void createRoute(RouteCreator routeCreator);

}

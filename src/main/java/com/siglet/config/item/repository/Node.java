package com.siglet.config.item.repository;

import com.siglet.config.item.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class Node<T extends Item> {


    private final String name;

    private final T item;

    public Node(String name, T item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }


    public T getItem() {
        return item;
    }

}

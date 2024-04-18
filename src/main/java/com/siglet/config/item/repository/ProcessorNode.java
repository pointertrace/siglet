package com.siglet.config.item.repository;

import com.siglet.config.item.ProcessorItem;

public abstract class ProcessorNode<T extends ProcessorItem> extends Node<T> {

    public ProcessorNode(String name, T item) {
        super(name, item);
    }
}

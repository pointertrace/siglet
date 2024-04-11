package com.siglet.config.item.repository;

import com.siglet.config.item.GrpcReceiverItem;
import com.siglet.config.item.ReceiverItem;

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
}

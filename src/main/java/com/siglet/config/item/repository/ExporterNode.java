package com.siglet.config.item.repository;

import com.siglet.config.item.ExporterItem;
import com.siglet.config.item.GrpcExporterItem;

import java.util.ArrayList;
import java.util.List;

public class ExporterNode extends Node<ExporterItem> {

    private List<Node<?>> from = new ArrayList<>();

    public ExporterNode(String name, ExporterItem exporterItem) {
        super(name, exporterItem);
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

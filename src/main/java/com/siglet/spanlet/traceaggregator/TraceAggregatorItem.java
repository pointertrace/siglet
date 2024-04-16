package com.siglet.spanlet.traceaggregator;

import com.siglet.config.item.Item;

import java.util.List;

public class TraceAggregatorItem extends Item {

    private List<String> to;

    private String type = "default";

    private Object config;

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public void setToSingleValue(String to) {
        this.to = List.of(to);
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }
}

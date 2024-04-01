package com.siglet.config.builder;

import org.w3c.dom.ls.LSException;

import java.util.List;

public class SpanletBuilder {

    private String name;

    private String type;

    private Object config;

    private List<String> to;

    private List<String> from;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getConfig() {
        return config;
    }

    public void setConfig(Object config) {
        this.config = config;
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

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

    public void setFromSingleValue(String from) {
        this.from = List.of(from);
    }

    public void setFromOneValue(String from) {
        this.from = List.of(from);
    }
}

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

    public void setTo(Object to) {
        if (to instanceof String str) {
            this.to = List.of(str);
        } else if (to instanceof List<?> lst) {
            this.to = lst.stream().map(Object::toString).toList();
        } else {
            throw new IllegalArgumentException("parameter must be a string or a list of strings but it is a " +
                    to.getClass().getName());
        }
    }

    public List<String> getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        if (from instanceof String str) {
            this.from = List.of(str);
        } else if (from instanceof List<?> lst) {
            this.from = lst.stream().map(Object::toString).toList();
        } else {
            throw new IllegalArgumentException("parameter must be a string or a list of stirngs");
        }
    }

    public void setFrom(List<String> from) {
        this.from = from;
    }

    public void setFromOneValue(String from) {
        this.from = List.of(from);
    }
}

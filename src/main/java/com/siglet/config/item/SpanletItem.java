package com.siglet.config.item;

import java.util.List;

public class SpanletItem extends ProcessorItem {

    private String type;

    private Object config;


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

}

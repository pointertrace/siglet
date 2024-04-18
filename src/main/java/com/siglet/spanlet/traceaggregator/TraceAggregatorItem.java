package com.siglet.spanlet.traceaggregator;

import com.siglet.config.item.ProcessorItem;

public class TraceAggregatorItem extends ProcessorItem {

    private String type = "default";

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

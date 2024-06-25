package com.siglet.config.item;

public class MetricletItem extends ProcessorItem {

    private ValueItem<String> type;

    public ValueItem<String> getType() {
        return type;
    }

    public void setType(ValueItem<String> type) {
        this.type = type;
    }

}

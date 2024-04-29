package com.siglet.spanlet.traceaggregator;

import com.siglet.config.item.ProcessorItem;
import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.locatednode.Location;

public class TraceAggregatorItem extends ProcessorItem {

    private ValueItem<String> type = new ValueItem<>(Location.of(-1,-1),"default");

    public void setType(ValueItem<String> type) {
        this.type = type;
    }

    public ValueItem<String> getType() {
        return type;
    }
}

package com.siglet.pipeline.common.processor;

import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;

public class ProcessorConfig extends Item {

    private ValueItem<String> action;

    public ValueItem<String> getAction() {
        return action;
    }

    public void setAction(ValueItem<String> action) {
        this.action = action;
    }
}

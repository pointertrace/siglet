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

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  processorConfig");

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getAction().getLocation().describe());
        sb.append("  action");
        sb.append("\n");
        sb.append(getAction().describe(level + 2));


        return sb.toString();
    }
}

package com.siglet.pipeline.processor.common.action;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;

// todo testar
public class ActionConfig extends Item {

    private String action;

    private Location actionLocation;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Location getActionLocation() {
        return actionLocation;
    }

    public void setActionLocation(Location actionLocation) {
        this.actionLocation = actionLocation;
    }
    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  processorConfig");

        sb.append("\n");
        sb.append(getDescriptionPrefix(level + 1));
        sb.append(getActionLocation().describe());
        sb.append("  action: ");
        sb.append(getAction());

        return sb.toString();
    }

}

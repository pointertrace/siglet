package com.siglet.pipeline.processor.common.action;

import com.siglet.config.item.Describable;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public class ActionConfig  implements Located, Describable {

    private Location location;

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
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  actionConfig:");

        sb.append("\n");
        sb.append(prefix(level + 1));
        sb.append(getActionLocation().describe());
        sb.append("  action: ");
        sb.append(getAction());

        return sb.toString();
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}

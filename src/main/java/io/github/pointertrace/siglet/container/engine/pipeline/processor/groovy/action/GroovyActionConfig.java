package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siget.parser.located.Located;
import io.github.pointertrace.siget.parser.located.Location;

public class GroovyActionConfig implements Located, Describable {

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
        StringBuilder sb = new StringBuilder(Describable.PREFIX.repeat(level));
        sb.append(getLocation().describe());
        sb.append("  groovyActionConfig:");

        sb.append("\n");
        sb.append(Describable.PREFIX.repeat(level + 1));
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

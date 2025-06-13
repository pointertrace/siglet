package com.siglet.container.engine.pipeline.processor.groovy.router;

import com.siglet.api.parser.Describable;
import com.siglet.api.parser.located.Located;
import com.siglet.api.parser.located.Location;

public class RouteConfig implements Located, Describable {

    private Location location;

    private String when;

    private Location whenLocation;

    private String to;

    private Location toLocation;

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Location getWhenLocation() {
        return whenLocation;
    }

    public void setWhenLocation(Location whenLocation) {
        this.whenLocation = whenLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.PREFIX.repeat(level));
        sb.append(getLocation().describe());
        sb.append("  route:");

        sb.append("\n");
        sb.append(Describable.PREFIX.repeat(level + 1));
        sb.append(getWhenLocation().describe());
        sb.append("  when: ");
        sb.append(getWhen());

        sb.append("\n");
        sb.append(Describable.PREFIX.repeat(level + 1));
        sb.append(getToLocation().describe());
        sb.append("  to: ");
        sb.append(getTo());

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

package com.siglet.container.config.raw;

import com.siglet.api.parser.Describable;
import com.siglet.api.parser.located.Located;
import com.siglet.api.parser.located.Location;

public abstract class BaseConfig implements Located, Describable {

    private Location location;

    private String name;

    private Location nameLocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(Location nameLocation) {
        this.nameLocation = nameLocation;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void afterSetValues() {
    }

    public String describe() {
        return describe(0);
    }

    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getNameLocation().describe());
        sb.append("  name: ");
        sb.append(getName());
        sb.append("\n");

        return sb.toString();
    }

    protected String prefix(int level) {
        return Describable.PREFIX.repeat(level);
    }


}
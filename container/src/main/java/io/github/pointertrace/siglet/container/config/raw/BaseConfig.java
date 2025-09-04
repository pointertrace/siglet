package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

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
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getNameLocation().describe());
        sb.append("  name: ");
        sb.append(getName());
        sb.append("\n");

        return sb.toString();
    }

}
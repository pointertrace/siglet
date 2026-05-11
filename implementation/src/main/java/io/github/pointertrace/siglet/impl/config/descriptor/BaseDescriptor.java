package io.github.pointertrace.siglet.impl.config.descriptor;


import io.github.pointertrace.siglet.parser.Locatable;
import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.StringValue;

public abstract class BaseDescriptor implements ValidatableDescriptor, Locatable {

    private Location location;

    private StringValue name;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public StringValue getName() {
        return name;
    }

    public void setName(StringValue name) {
        this.name = name;
    }

    public void afterSetValues() {
    }

}
package com.siglet.config.parser.node;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public abstract sealed class Node implements Located permits ObjectNode, ArrayNode, ValueNode {

    private static final String PREFIX = "  ";

    private Location location;

    private ValueSetter valueSetter;

    private ValueCreator valueCreator;

    private LocationSetter locationSetter = LocationSetter.EMPTY;

    protected Node(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public void setValueSetter(ValueSetter valueSetter) {
        this.valueSetter = valueSetter;
    }

    public void setLocationSetter(LocationSetter locationSetter) {
        this.locationSetter = locationSetter;
    }

    public LocationSetter getLocationSetter() {
        return locationSetter;
    }

    public abstract Object getValue();

    public String describe() {
        return describe(0);
    }

    protected abstract String describe(int level);

    protected String getDescriptionPrefix(int level) {
        return PREFIX.repeat(level);
    }


    public ValueCreator getValueCreator() {
        return valueCreator;
    }

    public void setValueCreator(ValueCreator valueCreator) {
        this.valueCreator = valueCreator;
    }
}

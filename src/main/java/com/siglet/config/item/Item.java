package com.siglet.config.item;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

public abstract class Item implements Located {

    private static final String PREFIX = "  ";

    private Location location;

    private ValueItem<String> name;

    public ValueItem<String> getName() {
        return name;
    }

    public void setName(ValueItem<String> name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void afterSetValues() {
    }

    protected String getDescriptionPrefix(int level) {
        return PREFIX.repeat(level);
    }

    public String describe() {
        return describe(0);
    }

    public String describe(int level) {
        return "need description for " + getClass().getName();
    }


}
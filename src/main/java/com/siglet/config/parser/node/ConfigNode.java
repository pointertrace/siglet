package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

import java.util.Collections;
import java.util.List;

public abstract sealed class ConfigNode implements Located permits ObjectConfigNode, ArrayConfigNode, ValueConfigNode {

    private static final String PREFIX = "  ";

    private Location location;

    private ValueSetter valueSetter;

    protected ConfigNode(Location location) {
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

    public abstract Item getValue();

    public String describe() {
        return describe(0);
    }

    protected abstract String describe(int level);

    protected String getDescriptionPrefix(int level) {
        return PREFIX.repeat(level);
    }


}

package com.siglet.config.parser.node;

import com.siglet.config.parser.locatednode.Located;
import com.siglet.config.parser.locatednode.Location;

public abstract class ConfigNode implements Located {

    private final Location location;

    private ValueSetter valueSetter;
    protected ConfigNode(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }


    public ValueSetter getValueSetter() {
        return valueSetter;
    }

    public void setValueSetter(ValueSetter valueSetter) {
        this.valueSetter = valueSetter;
    }

    public abstract Object getValue();

    public abstract void clear();
}

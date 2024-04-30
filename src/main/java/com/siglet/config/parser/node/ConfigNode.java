package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.parser.locatednode.Located;
import com.siglet.config.parser.locatednode.Location;

public sealed abstract class ConfigNode implements Located permits ObjectConfigNode, ArrayConfigNode, ValueConfigNode {

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

}

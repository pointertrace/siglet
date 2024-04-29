package com.siglet.config.item;

import com.siglet.config.parser.locatednode.Located;
import com.siglet.config.parser.locatednode.Location;

public class Item implements Located {

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
}

package com.siglet.config.item;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

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

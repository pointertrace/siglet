package com.siglet.config.item;

import com.siglet.config.parser.locatednode.Location;

import java.util.List;

public class ArrayItem<T extends Item> extends Item {

    private final List<T> values;

    public ArrayItem(Location location,List<T> values) {
        setLocation(location);
        this.values = values;
    }


    public List<T> getValue() {
        return values;
    }

}

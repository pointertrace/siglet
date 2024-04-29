package com.siglet.config.item;

import com.siglet.config.parser.locatednode.Location;

import java.util.Objects;

public class ValueItem<T> extends Item {

    private final T value;

    public ValueItem(Location location, T value) {
        setLocation(location);
        this.value = value;
    }


    public T getValue() {
        return value;
    }

}

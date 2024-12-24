package com.siglet.config.item;

import com.siglet.config.located.Location;

public class ValueItem<T> extends Item {

    private final T value;

    public ValueItem(Location location, T value) {
        setLocation(location);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String describe(int level) {
        return getDescriptionPrefix(level) + getLocation().describe() + (value != null ?
                "  " + value.getClass().getSimpleName() + "  (" + value + ")" :
                "null");
    }
}

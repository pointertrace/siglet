package com.siglet.container.config.raw;


import com.siglet.parser.located.Location;

public class ValueConfig<T> extends BaseConfig {

    private final T value;

    public ValueConfig(Location location, T value) {
        setLocation(location);
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String describe(int level) {
        return prefix(level) + getLocation().describe() + (value != null ?
                "  " + value.getClass().getSimpleName() + "  (" + value + ")" :
                "null");
    }
}

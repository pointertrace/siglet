package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siget.parser.located.Location;

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
        return Describable.prefix(level) + getLocation().describe() + (value != null ?
                "  " + value.getClass().getSimpleName() + "  (" + value + ")" :
                "null");
    }
}

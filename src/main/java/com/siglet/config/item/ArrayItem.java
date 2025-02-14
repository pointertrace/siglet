package com.siglet.config.item;

import com.siglet.config.located.Location;

import java.util.List;
import java.util.stream.Stream;

public class ArrayItem<T extends Item> extends Item {

    private final List<T> values;

    public ArrayItem(Location location,List<T> values) {
        setLocation(location);
        this.values = values;
    }


    public List<T> getValue() {
        return values;
    }

    public Stream<T> stream() {
        return values.stream();
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  arrayItem");
        for (Item child : values) {
            sb.append("\n");
            sb.append(prefix(level + 1));
            sb.append(child.getLocation().describe());
            sb.append("  array item");
            sb.append("\n");
            sb.append(child.describe(level + 2));
        }
        return sb.toString();
    }
}

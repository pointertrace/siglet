package com.siglet.config.parser.node;

import com.siglet.config.parser.locatednode.Location;

import java.util.ArrayList;
import java.util.List;

public class ArrayConfigNode extends ConfigNode {


    private final List<ConfigNode> items;

    protected ArrayConfigNode(List<ConfigNode> items, Location location) {
        super(location);
        this.items = new ArrayList<>(items);
    }

    public int getLength() {
        return items.size();
    }

    public ConfigNode getItem(int i) {
        return items.get(i);
    }

    @Override
    public Object getValue() {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < getLength(); i++) {
            result.add(items.get(i).getValue());
        }
        return result;
    }

    @Override
    public void clear() {
        setValueSetter(null);
        for(ConfigNode item: items) {
            item.clear();
        }
    }
}

package com.siglet.config.parser.node;

import com.siglet.config.item.ArrayItem;
import com.siglet.config.item.Item;
import com.siglet.config.located.Location;

import java.util.ArrayList;
import java.util.List;

public final class ArrayConfigNode extends ConfigNode {


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
    public Item getValue() {
        List<Item> itemList = new ArrayList<>();
        for (int i = 0; i < getLength(); i++) {
            itemList.add(items.get(i).getValue());
        }
        return new ArrayItem(getLocation(), itemList);
    }

}

package com.siglet.config.parser.node;

import com.siglet.config.item.ArrayItem;
import com.siglet.config.item.Item;
import com.siglet.config.located.Location;

import java.util.ArrayList;
import java.util.List;

public final class ArrayNode extends Node {


    private final List<Node> items;

    ArrayNode(List<Node> items, Location location) {
        super(location);
        this.items = new ArrayList<>(items);
    }

    public int getLength() {
        return items.size();
    }

    public Node getItem(int i) {
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

    @Override
    protected String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  array");
        for (Node child : items) {
            sb.append("\n");
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(child.getLocation().describe());
            sb.append("  array item");
            sb.append("\n");
            sb.append(child.describe(level + 2));
        }
        return sb.toString();
    }
}

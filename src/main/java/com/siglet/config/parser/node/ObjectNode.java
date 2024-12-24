package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;

import java.util.*;

public final class ObjectNode extends Node {

    private final Map<String, Node> children = new HashMap<>();
    private final Map<String, Location> childrenKeyLocation = new HashMap<>();

    private ValueCreator valueCreator;

    ObjectNode(List<Property> childrenProperties, Location location) {
        super(location);
        childrenProperties.forEach(prop -> {
            children.put(prop.getKey().getValue(), prop.getValue());
            childrenKeyLocation.put(prop.getKey().getValue(), prop.getKey().getLocation());
        });
    }

    public int getSize() {
        return children.size();
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(children.keySet());
    }

    public Node get(String key) {
        return children.get(key);
    }

    public Location getPropertyKeyLocation(String key) {
        return childrenKeyLocation.get(key);
    }

    public Map<String, Node> getProperties() {
        return Collections.unmodifiableMap(this.children);
    }

    public ValueCreator getValueCreator() {
        return valueCreator;
    }

    public void setValueCreator(ValueCreator valueCreator) {
        this.valueCreator = valueCreator;
    }

    public Item getValue() {
        Item result = getValueCreator().create();
        result.setLocation(getLocation());

        for (Node prop : getProperties().values()) {
            Item propValue = prop.getValue();
            prop.getValueSetter().set(result, propValue);
        }

        return result;
    }

    @Override
    protected String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  object");
        for (Map.Entry<String, Node> property: children.entrySet()) {
            sb.append("\n");
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(property.getValue().getLocation().describe());
            sb.append("  property  (");
            sb.append(property.getKey());
            sb.append(")");
            sb.append("\n");
            sb.append(property.getValue().describe(level + 2));
        }
        return sb.toString();
    }
    public void adjustLocation() {

        for (String propName : getPropertyNames()) {
            Node propNode = get(propName);
            propNode.getValue().setLocation(getPropertyKeyLocation(propName));
        }

    }

    public static class Key {
        private final String value;

        private final Location location;

        public Key(String value, Location location) {
            this.value = value;
            this.location = location;
        }

        public String getValue() {
            return value;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static class Property {

        private final Key key;

        private final Node value;


        public Property(Key key, Node value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public Node getValue() {
            return value;
        }

    }

}

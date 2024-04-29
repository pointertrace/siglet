package com.siglet.config.parser.node;

import com.siglet.config.item.Item;
import com.siglet.config.parser.locatednode.Location;

import java.util.*;

public final class ObjectConfigNode extends ConfigNode {

    private final Map<String, ConfigNode> children = new HashMap<>();
    private final Map<String, Location> childrenKeyLocation = new HashMap<>();

    private ValueCreator valueCreator;

    protected ObjectConfigNode(List<Property> childrenProperties, Location location) {
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

    public ConfigNode get(String key) {
        return children.get(key);
    }

    public Location getPropertyKeyLocation(String key) {
        return childrenKeyLocation.get(key);
    }

    public Map<String, ConfigNode> getProperties() {
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
//        result.setLocation(getLocation());

        for (ConfigNode prop : getProperties().values()) {
            Item propValue = prop.getValue();
            prop.getValueSetter().set(result, propValue);
        }

        return result;
    }

    public void adjustLocation() {

        for (String propName : getPropertyNames()) {
            ConfigNode propNode = get(propName);
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

        private final ConfigNode value;


        public Property(Key key, ConfigNode value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public ConfigNode getValue() {
            return value;
        }

    }

}

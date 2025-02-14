package com.siglet.config.parser.node;

import com.siglet.SigletError;
import com.siglet.config.located.Located;
import com.siglet.config.located.Location;

import java.util.*;

public final class ObjectNode extends Node {

    private final Map<String, Node> children = new HashMap<>();

    private final Map<String, Location> childrenKeyLocation = new HashMap<>();

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

    @Override
    public Object getValue() {
        Object result = getValueCreator().create();
        Located.set(result, getLocation());

        for (Node prop : getProperties().values()) {
            Object propValue = prop.getValue();
            if (result instanceof Located locatedPropValue && prop.getLocationSetter() != null) {
                prop.getLocationSetter().setLocation(locatedPropValue, prop.getLocation());
            }
            try {
                prop.getValueSetter().set(result, propValue);
            } catch (Exception e) {
                throw new SigletError("Error setting value to property in node [" + prop + "]. at " +
                        prop.getLocation().describe() + ". " + e);
            }
        }

        return result;
    }

    public void adjustLocation() {

        for (String propName : getPropertyNames()) {
            Node propNode = get(propName);
            if (propNode.getValue() instanceof Located locatedValue) {
                locatedValue.setLocation(getPropertyKeyLocation(propName));
            }
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

    @Override
    protected String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  object");
        for (Map.Entry<String, Node> property : children.entrySet()) {
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
}

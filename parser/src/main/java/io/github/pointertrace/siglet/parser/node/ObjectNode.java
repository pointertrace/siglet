package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.NodeValueBuilder;
import io.github.pointertrace.siglet.parser.ParserError;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;

import java.util.*;

public final class ObjectNode extends BaseNode {

    private final Map<String, BaseNode> children = new HashMap<>();

    private final Map<String, Location> childrenKeyLocation = new HashMap<>();

    private final Map<String, ValueSetter> childrenKeyValueSetter = new HashMap<>();

    private final Map<String, LocationSetter> childrenKeyLocationSetter = new HashMap<>();

    ObjectNode(List<Property> childrenProperties, Location location) {
        super(location);
        childrenProperties.forEach(prop -> {
            children.put(prop.getKey().getValue(), prop.getValue());
            childrenKeyValueSetter.put(prop.getKey().getValue(), null);
            childrenKeyLocationSetter.put(prop.getKey().getValue(), null);
            childrenKeyLocation.put(prop.getKey().getValue(), prop.getKey().getLocation());
        });
    }

    public int getSize() {
        return children.size();
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(children.keySet());
    }

    public BaseNode get(String key) {
        return children.get(key);
    }

    public Location getPropertyKeyLocation(String key) {
        return childrenKeyLocation.get(key);
    }

    public Map<String, BaseNode> getProperties() {
        return Collections.unmodifiableMap(this.children);
    }

    @Override
    public Object getValue() {
        Object result = getValueCreator().create();
        Located.set(result, getLocation());

        for (BaseNode prop : getProperties().values()) {
            Object propValue = prop.getValue();
            if (prop.getLocationSetter() != null) {
                prop.getLocationSetter().setLocation(result, prop.getLocation());
            }
            try {
                prop.getValueSetter().set(result, propValue);
            } catch (Exception e) {
                throw new ParserError("Error setting value to property in node [" + prop + "]. at " +
                                      prop.getLocation().describe() + ". " + e);
            }
        }

        for (Map.Entry<String, ValueSetter> keyValueSetter : childrenKeyValueSetter.entrySet()) {
            if (keyValueSetter.getValue() != null) {
                keyValueSetter.getValue().set(result, keyValueSetter.getKey());
            }
        }

        for (Map.Entry<String, LocationSetter> keyValueSetter : childrenKeyLocationSetter.entrySet()) {

            if (keyValueSetter.getValue() != null) {
                keyValueSetter.getValue().setLocation(result, childrenKeyLocation.get(keyValueSetter.getKey()));
            }
        }
        if (result instanceof NodeValueBuilder) {
            NodeValueBuilder nodeValueBuilder = (NodeValueBuilder) result;
            return nodeValueBuilder.build();
        } else {
            return result;
        }
    }

    public void addKeySetter(String key, ValueSetter valueSetter) {
        if (!childrenKeyValueSetter.containsKey(key)) {
            throw new ParserError("Could not find property Key [" + key + "]");
        }
        childrenKeyValueSetter.put(key, valueSetter);
    }

    public void addKeyLocationSetter(String key, LocationSetter locationSetter) {
        if (!childrenKeyLocationSetter.containsKey(key)) {
            throw new ParserError("Could not find property Key [" + key + "]");
        }
        childrenKeyLocationSetter.put(key, locationSetter);
    }

    public void adjustLocation() {

        for (String propName : getPropertyNames()) {
            BaseNode propNode = get(propName);
            if (propNode.getValue() instanceof Located) {
                Located locatedValue = (Located) propNode.getValue();
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

        private final BaseNode value;


        public Property(Key key, BaseNode value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public BaseNode getValue() {
            return value;
        }

    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  object");
        for (Map.Entry<String, BaseNode> property : children.entrySet()) {
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

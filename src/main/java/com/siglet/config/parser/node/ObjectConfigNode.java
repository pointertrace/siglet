package com.siglet.config.parser.node;

import com.siglet.config.parser.locatednode.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectConfigNode extends ConfigNode {

    private final Map<String, ConfigNode> children;

    private ValueCreator valueCreator;

    protected ObjectConfigNode(Map<String, ConfigNode> children, Location location) {
        super(location);
        this.children = new HashMap<>(children);
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

    public Map<String, ConfigNode> getProperties() {
        return Collections.unmodifiableMap(this.children);
    }

    public ValueCreator getValueCreator() {
        return valueCreator;
    }

    public void setValueCreator(ValueCreator valueCreator) {
        this.valueCreator = valueCreator;
    }

    public Object getValue() {
        Object result = getValueCreator().create();

        for (ConfigNode prop : getProperties().values()) {
            Object propValue = prop.getValue();
            prop.getValueSetter().set(result, propValue);
        }

        return result;
    }

    @Override
    public void clear() {
//        for (ConfigNode child : children.values()) {
//            child.clear();
//        }
    }
}

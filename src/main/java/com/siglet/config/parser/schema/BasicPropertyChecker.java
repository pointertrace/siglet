package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueSetter;

public abstract class BasicPropertyChecker implements NodeChecker {


    private final String propertyName;

    private final boolean required;

    private final ValueSetter valueSetter;

    protected BasicPropertyChecker(ValueSetter valueSetter, String propertyName, boolean required) {
        this.valueSetter = valueSetter;
        this.propertyName = propertyName;
        this.required = required;
    }

    public ConfigNode basicCheck(ConfigNode node) throws SchemaValidationException {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationException("is not a object", node.getLocation());
        }
        ConfigNode propNode = objectNode.get(propertyName);
        if (required && propNode == null) {
            throw new SingleSchemaValidationException("must have a " + propertyName + " property!", node.getLocation());
        }
        return propNode;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isRequired() {
        return required;
    }

    public ValueSetter getValueSetter() {
        return valueSetter;
    }
}

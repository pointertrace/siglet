package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;

public abstract class AbstractPropertyChecker implements NodeChecker {


    private final String propertyName;

    private final boolean required;

    protected AbstractPropertyChecker(String propertyName, boolean required) {
        this.propertyName = propertyName;
        this.required = required;
    }

    public ConfigNode propertyPresenceCheck(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError("is not a object", node.getLocation());
        }
        ConfigNode propNode = objectNode.get(propertyName);
        if (required && propNode == null) {
            throw new SingleSchemaValidationError("must have a " + propertyName + " property!", node.getLocation());
        }
        return propNode;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isRequired() {
        return required;
    }

}

package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;

public abstract class AbstractPropertyChecker extends NodeChecker {


    private final String propertyName;

    private final boolean required;

    protected AbstractPropertyChecker(String propertyName, boolean required) {
        this.propertyName = propertyName;
        this.required = required;
    }

    public ConfigNode propertyPresenceCheck(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a object");
        }
        ConfigNode propNode = objectNode.get(propertyName);
        if (required && propNode == null) {
            throw new SingleSchemaValidationError(node.getLocation(),"must have a " + propertyName + " property!");
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

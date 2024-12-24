package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;

public abstract class AbstractPropertyChecker extends NodeChecker {


    private final String propertyName;

    private final boolean required;

    protected AbstractPropertyChecker(String propertyName, boolean required) {
        this.propertyName = propertyName;
        this.required = required;
    }

    public Node propertyPresenceCheck(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a object");
        }
        Node propNode = objectNode.get(propertyName);
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

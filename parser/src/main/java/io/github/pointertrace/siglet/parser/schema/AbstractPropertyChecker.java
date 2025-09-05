package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;

import java.util.Set;

public abstract class AbstractPropertyChecker extends BaseNodeChecker {


    private final PropertyKeyChecker propertyKeyChecker;

    private final boolean required;

    protected AbstractPropertyChecker(PropertyKeyChecker propertyKeyChecker, boolean required) {
        this.propertyKeyChecker = propertyKeyChecker;
        this.required = required;
    }

    public BaseNode propertyPresenceCheck(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "is not a object");
        }
        ObjectNode objectNode = (ObjectNode) node;


        BaseNode propNode = propertyKeyChecker.getPropertyNode(objectNode);
        if (required && propNode == null) {
            throw propertyKeyChecker.createPropertyNotFoundError(objectNode);
        }
        return propNode;
    }

    public String getPropertyName() {
        return propertyKeyChecker.getPropertyKeyNames();
    }

    public Set<String> getValidKeys() {
        return propertyKeyChecker.getValidKeys();
    }

    public boolean isRequired() {
        return required;
    }

    public PropertyKeyChecker getPropertyKeyChecker() {
        return propertyKeyChecker;
    }
}

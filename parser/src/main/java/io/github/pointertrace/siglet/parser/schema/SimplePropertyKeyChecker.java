package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

public class SimplePropertyKeyChecker implements PropertyKeyChecker {

    private final String propertyName;

    public SimplePropertyKeyChecker(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean isValid(String name) {
        return propertyName.equals(name);
    }

    @Override
    public Set<String> getValidKeys() {
        Set<String> validKeys = new HashSet<>();
        validKeys.add(propertyName);
        return validKeys;
    }

    @Override
    public BaseNode getPropertyNode(ObjectNode objectNode) {
        return objectNode.get(propertyName);
    }

    @Override
    public String getPropertyKeyNames() {
        return propertyName;
    }

    @Override
    public SingleSchemaValidationError createPropertyNotFoundError(ObjectNode node) {
        return new SingleSchemaValidationError(node.getLocation(),
                String.format("Must have %s as property", propertyName));
    }
}

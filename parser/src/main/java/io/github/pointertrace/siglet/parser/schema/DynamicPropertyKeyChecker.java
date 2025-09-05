package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

public class DynamicPropertyKeyChecker implements PropertyKeyChecker {

    private final Set<String> validKeys;

    public DynamicPropertyKeyChecker(Set<String> validKeys) {
        this.validKeys = new HashSet<>(validKeys);
    }

    @Override
    public boolean isValid(String name) {
        return validKeys.contains(name);
    }

    @Override
    public Set<String> getValidKeys() {
        return validKeys;
    }

    @Override
    public BaseNode getPropertyNode(ObjectNode objectNode) {
        for (String key : validKeys) {
            BaseNode node = objectNode.get(key);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    @Override
    public String getPropertyKeyNames() {
        return String.join(", ", validKeys);
    }

    @Override
    public SingleSchemaValidationError createPropertyNotFoundError(ObjectNode node) {
        return new SingleSchemaValidationError(node.getLocation(),
                String.format("Expecting one of: %s by available keys are: %s", getPropertyKeyNames(),
                        String.join(", ", node.getPropertyNames())));

    }
}

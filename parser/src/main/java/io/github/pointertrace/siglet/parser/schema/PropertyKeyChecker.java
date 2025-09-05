package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.node.BaseNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;

import java.util.Set;

public interface PropertyKeyChecker {

    boolean isValid(String name);

    Set<String> getValidKeys();

    BaseNode getPropertyNode(ObjectNode objectNode);

    String getPropertyKeyNames();

    SingleSchemaValidationError createPropertyNotFoundError(ObjectNode node);

}

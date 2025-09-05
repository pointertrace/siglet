package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ObjectNode;

public class EmptyPropertyChecker extends BaseNodeChecker {

    private final String emptyPropertyName;


    public EmptyPropertyChecker(String emptyPropertyName) {
        this.emptyPropertyName = emptyPropertyName;
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "is not a object");
        }
        ObjectNode objectNode = (ObjectNode) node;
        Node emptyPropertyNode = objectNode.get(emptyPropertyName);
        if (emptyPropertyNode != null) {
            throw new SingleSchemaValidationError(emptyPropertyNode.getLocation(),
                    "must not have a " + emptyPropertyName + " property!");
        }
    }

    @Override
    public String getName() {
        return "empty";
    }
}

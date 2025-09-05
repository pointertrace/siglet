package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ValueNode;

public class BigDecimalChecker extends BaseNodeChecker {

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.BigDecimalNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a big decimal value!");
        }
    }

    @Override
    public String getName() {
        return "bigDecimal";
    }
}

package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

public class BigDecimalChecker extends NodeChecker {

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.BigDecimal)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a big decimal value!");
        }
    }

    @Override
    public String getName() {
        return "bigDecimal";
    }
}

package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

public class BigIntegerChecker extends NodeChecker {

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.BigInteger)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a big integer value!");
        }
    }

    @Override
    public String getName() {
        return "bigInteger";
    }
}

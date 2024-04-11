package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

public class BigIntegerChecker implements NodeChecker {

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.BigInteger)) {
            throw new SingleSchemaValidationError("is not a big decimal value!", node.getLocation());
        }
    }

    @Override
    public String getName() {
        return "bigInteger";
    }
}

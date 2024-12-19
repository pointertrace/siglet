package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

public class BigIntegerChecker extends NodeChecker {

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.BigInteger)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a big integer value!");
        }
    }

    @Override
    public String getName() {
        return "bigInteger";
    }
}

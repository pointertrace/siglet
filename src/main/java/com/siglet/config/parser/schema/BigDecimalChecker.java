package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

public class BigDecimalChecker extends NodeChecker {

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.BigDecimal)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a big decimal value!");
        }
    }

    @Override
    public String getName() {
        return "bigDecimal";
    }
}

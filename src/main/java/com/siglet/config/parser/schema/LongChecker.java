package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

public class LongChecker implements NodeChecker {

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.Long)) {
            throw new SingleSchemaValidationError("is not a long value!",node.getLocation());
        }
    }

    @Override
    public String getName() {
        return "long";
    }
}

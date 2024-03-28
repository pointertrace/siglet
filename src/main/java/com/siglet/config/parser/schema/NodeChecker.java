package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueTransformer;

public interface NodeChecker {

    void check(ConfigNode node) throws SchemaValidationException;

    String getName();

    default ValueTransformer getValueTransformer() {
        return null;
    }
}

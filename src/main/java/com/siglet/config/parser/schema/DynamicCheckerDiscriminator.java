package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;

public interface DynamicCheckerDiscriminator {

    NodeChecker getChecker(ConfigNode configNode) throws SchemaValidationException;
}

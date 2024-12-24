package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;

public interface DynamicCheckerDiscriminator {

    NodeChecker getChecker(Node node) throws SchemaValidationError;
}

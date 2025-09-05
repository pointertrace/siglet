package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;

public interface DynamicCheckerDiscriminator {

    NodeChecker getChecker(Node node) throws SchemaValidationError;
}

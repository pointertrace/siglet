package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

import java.util.List;

public class LongChecker extends NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public LongChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.Long longNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a long value!");
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(longNode);
        }
    }

    @Override
    public String getName() {
        return "long";
    }
}

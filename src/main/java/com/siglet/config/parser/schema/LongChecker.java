package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

import java.util.List;

public class LongChecker extends NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public LongChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.Long longNode)) {
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

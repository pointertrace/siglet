package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

import java.util.List;

public class AnyNumberChecker implements NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public AnyNumberChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.NumberConfigNode numberNode)) {
            throw new SingleSchemaValidationError("is not a number value!", node.getLocation());
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(numberNode);
        }
    }

    @Override
    public String getName() {
        return "number";
    }
}

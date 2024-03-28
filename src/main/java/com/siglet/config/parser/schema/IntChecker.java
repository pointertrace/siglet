package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

import java.util.List;

public class IntChecker implements NodeChecker {
    private final List<NodeChecker> additionalCheckers;

    public IntChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationException {
        if (!(node instanceof ValueConfigNode.Int intNode)) {
            throw new SingleSchemaValidationException("is not a int value!", node.getLocation());
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(intNode);
        }
    }

    @Override
    public String getName() {
        return "int";
    }


}

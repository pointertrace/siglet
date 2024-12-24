package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

import java.util.List;

public class IntChecker extends NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public IntChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.Int intNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a int value!");
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

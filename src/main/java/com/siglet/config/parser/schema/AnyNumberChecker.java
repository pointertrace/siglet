package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

import java.util.List;

public class AnyNumberChecker extends NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public AnyNumberChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.NumberNode numberNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a number value!");
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(numberNode);
        }
    }

    @Override
    public String getName() {
        return "number";
    }

    @Override
    public List<NodeChecker> getChildren() {
        return additionalCheckers;
    }
}

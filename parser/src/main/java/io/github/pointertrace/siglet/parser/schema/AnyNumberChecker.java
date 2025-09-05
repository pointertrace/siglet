package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ValueNode;

import java.util.Arrays;
import java.util.List;

public class AnyNumberChecker extends BaseNodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public AnyNumberChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = Arrays.asList(additionalCheckers);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.NumberNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a number value!");
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(node);
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

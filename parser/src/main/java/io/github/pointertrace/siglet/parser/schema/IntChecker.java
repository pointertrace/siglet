package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ValueNode;

import java.util.Arrays;
import java.util.List;

public class IntChecker extends BaseNodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public IntChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = Arrays.asList(additionalCheckers);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.IntNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a int value!");
        }
        for (NodeChecker additionalChecker : additionalCheckers) {
            additionalChecker.check(node);
        }
    }

    @Override
    public String getName() {
        return "int";
    }


}

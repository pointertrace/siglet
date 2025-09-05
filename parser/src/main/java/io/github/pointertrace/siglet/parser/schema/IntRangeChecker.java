package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ValueNode;

public class IntRangeChecker extends BaseNodeChecker {

    private final Integer lowInclusive;

    private final Integer highInclusive;

    public IntRangeChecker(Integer lowInclusive, Integer highInclusive) {
        this.lowInclusive = lowInclusive;
        this.highInclusive = highInclusive;
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.IntNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "must be an integer!");
        }
        ValueNode.IntNode intNode = (ValueNode.IntNode) node;
        if (lowInclusive != null && (int) intNode.getValue() < lowInclusive ||
                highInclusive != null && (int) intNode.getValue() > highInclusive) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("must be between %d and %d inclusive!", lowInclusive, highInclusive));
        }

    }

    @Override
    public String getName() {
        return "int range";
    }

    @Override
    public String getDescription() {
        return String.format("from %d to %d inclusive", lowInclusive, highInclusive);
    }
}

package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;

public class IntRangeChecker extends NodeChecker {

    private final Integer lowInclusive;

    private final Integer highInclusive;

    public IntRangeChecker(Integer lowInclusive, Integer highInclusive) {
        this.lowInclusive = lowInclusive;
        this.highInclusive = highInclusive;
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ValueNode.Int intNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "must be an integer!");
        }

        if (lowInclusive != null && (int) intNode.getValue().getValue() < lowInclusive ||
                highInclusive != null && (int) intNode.getValue().getValue() > highInclusive) {
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
        return String.format("from %d to %d inclusive",lowInclusive, highInclusive);
    }
}

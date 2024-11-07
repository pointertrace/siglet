package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;

public class IntRangeChecker implements NodeChecker {

    private final Integer lowInclusive;

    private final Integer highInclusive;

    public IntRangeChecker(Integer lowInclusive, Integer highInclusive) {
        this.lowInclusive = lowInclusive;
        this.highInclusive = highInclusive;
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ValueConfigNode.Int intNode)) {
            throw new SingleSchemaValidationError("must be an integer!", node.getLocation());
        }

        if (lowInclusive != null && (int) intNode.getValue().getValue() < lowInclusive ||
                highInclusive != null && (int) intNode.getValue().getValue() > highInclusive) {
            throw new SingleSchemaValidationError(String.format("must be between %d and %d inclusive!",
                    lowInclusive, highInclusive), node.getLocation());
        }

    }

    @Override
    public String getName() {
        return "int range [ " + lowInclusive + " to " + highInclusive + "] inclusive";
    }
}

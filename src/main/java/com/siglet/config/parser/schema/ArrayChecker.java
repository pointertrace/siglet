package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ArrayConfigNode;
import com.siglet.config.parser.node.ConfigNode;

import java.util.List;

public class ArrayChecker implements NodeChecker {


    private final List<NodeChecker> checks;

    public ArrayChecker(NodeChecker... checks) {
        this.checks = List.of(checks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (! (node  instanceof ArrayConfigNode arrayNode )) {
            throw new SingleSchemaValidationError("is not a array!", node.getLocation());
        }
        int length =  arrayNode.getLength();
        for (int i = 0; i < length; i++) {
            ConfigNode item = arrayNode.getItem(i);
            for (NodeChecker check : checks) {
                check.check(item);
            }
        }

    }

    @Override
    public String getName() {
        return "array";
    }
}

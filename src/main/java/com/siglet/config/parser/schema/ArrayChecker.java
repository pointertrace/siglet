package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ArrayNode;
import com.siglet.config.parser.node.Node;

import java.util.List;

public class ArrayChecker extends NodeChecker {


    private final List<NodeChecker> checks;

    public ArrayChecker(NodeChecker... checks) {
        this.checks = List.of(checks);
    }

    @Override
    public void check(Node node) throws SchemaValidationError {
        if (!(node instanceof ArrayNode arrayNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a array!");
        }
        int length = arrayNode.getLength();
        try {
            for (int i = 0; i < length; i++) {
                Node item = arrayNode.getItem(i);
                for (NodeChecker check : checks) {
                    check.check(item);
                }
            }
        } catch (SingleSchemaValidationError e) {
            throw new SingleSchemaValidationError(e.getLocation(),"array item is not valid",e);
        } catch (MultipleSchemaValidationError e) {
            throw new SingleSchemaValidationError(node.getLocation(),"array item is not valid",e);
        }
    }

    @Override
    public String getName() {
        return "array";
    }

    @Override
    public List<NodeChecker> getChildren() {
        return checks;
    }
}

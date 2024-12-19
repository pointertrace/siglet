package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ArrayConfigNode;
import com.siglet.config.parser.node.ConfigNode;

import java.util.List;

public class ArrayChecker extends NodeChecker {


    private final List<NodeChecker> checks;

    public ArrayChecker(NodeChecker... checks) {
        this.checks = List.of(checks);
    }

    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ArrayConfigNode arrayNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a array!");
        }
        int length = arrayNode.getLength();
        try {
            for (int i = 0; i < length; i++) {
                ConfigNode item = arrayNode.getItem(i);
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

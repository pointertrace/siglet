package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;
import com.siglet.config.parser.node.ValueTransformer;

import java.util.List;

public class TextChecker extends NodeChecker {

    private final List<NodeChecker> additionalCheckers;

    public TextChecker(NodeChecker... additionalCheckers) {
        this.additionalCheckers = List.of(additionalCheckers);
    }


    @Override
    public void check(ConfigNode node) throws SchemaValidationError {
        if (node instanceof ValueConfigNode.Text textNode) {
            for (NodeChecker additionalChecker : additionalCheckers) {
                additionalChecker.check(textNode);
                ValueTransformer valueTransformer = additionalChecker.getValueTransformer();
                if (valueTransformer != null) {
                    textNode.setValueTransformer(valueTransformer);
                }
            }

        } else {
            throw new SingleSchemaValidationError(node.getLocation(),"is not a text value!");
        }
    }

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public List<? extends NodeChecker> getChildren() {
        return additionalCheckers;
    }
}

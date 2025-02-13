package com.siglet.config.parser.schema;

import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ValueNode;
import com.siglet.config.parser.node.ValueTransformer;

import java.util.ArrayList;
import java.util.List;

public class TextChecker extends NodeChecker {

    private List<NodeChecker> additionalCheckers = new ArrayList<>();

    private ValueTransformer valueTransformer;

    public TextChecker(NodeChecker... additionalCheckers) {
        this(null, additionalCheckers);
    }

    public TextChecker(ValueTransformer valueTransformer,NodeChecker... additionalCheckers) {
        this.valueTransformer = valueTransformer;
        this.additionalCheckers = List.of(additionalCheckers);
    }


    @Override
    public void check(Node node) throws SchemaValidationError {
        if (node instanceof ValueNode.TextNode textNode) {
            for (NodeChecker additionalChecker : additionalCheckers) {
                additionalChecker.check(textNode);
                ValueTransformer checkValueTransformer = additionalChecker.getValueTransformer();
                if (checkValueTransformer != null) {
                    textNode.setValueTransformer(checkValueTransformer);
                }
            }
            if (valueTransformer != null) {
                textNode.setValueTransformer(valueTransformer);
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

package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.node.ValueNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextChecker extends BaseNodeChecker {

    private List<NodeChecker> additionalCheckers = new ArrayList<>();

    private ValueTransformer valueTransformer;

    public TextChecker(NodeChecker... additionalCheckers) {
        this(null, additionalCheckers);
    }

    public TextChecker(ValueTransformer valueTransformer, NodeChecker... additionalCheckers) {
        this.valueTransformer = valueTransformer;
        this.additionalCheckers = Arrays.asList(additionalCheckers);
    }


    @Override
    public void check(Node node) throws SchemaValidationError {
        if (node instanceof ValueNode.TextNode) {
            ValueNode.TextNode textNode = (ValueNode.TextNode) node;
            for (NodeChecker additionalChecker : additionalCheckers) {
                additionalChecker.check(node);
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

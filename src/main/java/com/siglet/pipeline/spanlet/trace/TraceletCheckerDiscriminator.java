package com.siglet.pipeline.spanlet.trace;

import com.siglet.SigletError;
import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationError;
import com.siglet.config.parser.schema.SingleSchemaValidationError;

public class TraceletCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final TraceletTypes traceletTypes;

    public TraceletCheckerDiscriminator(TraceletTypes traceletTypes) {
        this.traceletTypes = traceletTypes;
    }

    @Override
    public NodeChecker getChecker(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"must be an object!");
        }
        Node type = objectNode.get("type");
        if (type == null) {
            throw new SingleSchemaValidationError(node.getLocation(),"must have a type property!");
        }

        if (type.getValue() instanceof ValueItem valueItem){
            TraceletType traceletType = traceletTypes.get(valueItem.getValue().toString());
            return traceletType.getConfigDefinition().getChecker();
        } else {
            throw new SigletError("Tracelet type must be a ValueItem<String>");
        }
    }
}

package com.siglet.pipeline.spanlet.span;

import com.siglet.SigletError;
import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationError;
import com.siglet.config.parser.schema.SingleSchemaValidationError;

public class SpanletCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final SpanletTypes spanletTypes;

    public SpanletCheckerDiscriminator(SpanletTypes spanletTypes) {
        this.spanletTypes = spanletTypes;
    }

    @Override
    public NodeChecker getChecker(ConfigNode node) throws SchemaValidationError {
        if (!(node instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(),"must be an object!");
        }
        ConfigNode type = objectNode.get("type");
        if (type == null) {
            throw new SingleSchemaValidationError(node.getLocation(),"must have a type property!");
        }
        if (type.getValue() instanceof ValueItem valueItem){
            SpanletType spanletType = spanletTypes.get(valueItem.getValue().toString());
            return spanletType.getConfigDefinition().getChecker();
        } else {
            throw new SigletError("Spanlet type must be a ValueItem<String>");
        }
    }
}

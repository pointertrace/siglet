package com.siglet.pipeline.metriclet;

import com.siglet.SigletError;
import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationError;
import com.siglet.config.parser.schema.SingleSchemaValidationError;

public class MetricletCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final MetricletTypes metricletTypes;

    public MetricletCheckerDiscriminator(MetricletTypes metricletTypes) {
        this.metricletTypes = metricletTypes;
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
        if (type.getValue() instanceof ValueItem valueItem) {
            MetricletType metricletType = metricletTypes.get(valueItem.getValue().toString());
            return metricletType.getConfigDefinition().getChecker();
        } else {
            throw new SigletError("Metriclet type must be a ValueItem<String>");
        }
    }
}

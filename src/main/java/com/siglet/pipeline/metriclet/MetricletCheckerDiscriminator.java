package com.siglet.pipeline.metriclet;

import com.siglet.SigletError;
import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
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
    public NodeChecker getChecker(ConfigNode configNode) throws SchemaValidationError {
        if (!(configNode instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationError("must be an object!", configNode.getLocation());
        }
        ConfigNode type = objectNode.get("type");
        if (type == null) {
            throw new SingleSchemaValidationError("must have a type property!", objectNode.getLocation());
        }
        if (type.getValue() instanceof ValueItem valueItem) {
            MetricletType metricletType = metricletTypes.get(valueItem.getValue().toString());
            return metricletType.getConfigDefinition().getChecker();
        } else {
            throw new SigletError("Metriclet type must be a ValueItem<String>");
        }
    }
}

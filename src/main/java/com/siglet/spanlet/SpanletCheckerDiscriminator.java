package com.siglet.spanlet;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationError;
import com.siglet.config.parser.schema.SingleSchemaValidationError;

public class SpanletCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private SpanletTypes spanletTypes;

    public SpanletCheckerDiscriminator(SpanletTypes spanletTypes) {
        this.spanletTypes = spanletTypes;
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
        SpanletType spanletType = spanletTypes.get(type.getValue().toString());
        return spanletType.getConfigDefinition().getChecker();
    }
}

package com.siglet.spanlet;

import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationException;
import com.siglet.config.parser.schema.SingleSchemaValidationException;

public class SpanletCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private SpanletTypes spanletTypes;

    public SpanletCheckerDiscriminator(SpanletTypes spanletTypes) {
        this.spanletTypes = spanletTypes;
    }

    @Override
    public NodeChecker getChecker(ConfigNode configNode) throws SchemaValidationException {
        if (!(configNode instanceof ObjectConfigNode objectNode)) {
            throw new SingleSchemaValidationException("must be an object!", configNode.getLocation());
        }
        ConfigNode type = objectNode.get("type");
        if (type == null) {
            throw new SingleSchemaValidationException("must have a type property!", objectNode.getLocation());
        }
        SpanletType spanletType = spanletTypes.get(type.getValue().toString());
        return spanletType.getConfigDefinition().getChecker();
    }
}

package com.siglet.pipeline.processor;

import com.siglet.SigletError;
import com.siglet.config.item.SigletItem;
import com.siglet.config.item.SigletKind;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import com.siglet.config.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.SchemaValidationError;
import com.siglet.config.parser.schema.SingleSchemaValidationError;
import com.siglet.pipeline.processor.traceaggregator.TraceAggregatorConfig;

import static com.siglet.config.parser.schema.SchemaFactory.*;

public class ProcessorCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final ProcessorTypes processorTypes;

    public ProcessorCheckerDiscriminator(ProcessorTypes processorTypes) {
        this.processorTypes = processorTypes;
    }

    @Override
    public NodeChecker getChecker(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "must be an object!");
        }
        Node kind = objectNode.get("kind");
        if (kind == null) {
            throw new SingleSchemaValidationError(node.getLocation(), "must have a kind property!");
        }
        if (kind.getValue() instanceof SigletKind kindValue) {
            if (kindValue == SigletKind.TRACE_AGGREGATOR) {
                return traceAggregatorChecker();
            } else {
                Node type = objectNode.get("type");
                if (type == null) {
                    throw new SingleSchemaValidationError(node.getLocation(), "must have a type property!");
                }
                if (type.getValue() instanceof String typeValue) {
                    ProcessorType processorType = processorTypes.get(typeValue);
                    if (processorType == null) {
                        throw new SingleSchemaValidationError(node.getLocation(),
                                String.format("could not find [%s] as processor type", typeValue));
                    }
                    return processorType.getConfigDefinition().getChecker();
                } else {
                    throw new SingleSchemaValidationError(node.getLocation(),
                            String.format("processor type is a %s but should be a string",
                                    type.getValue().getClass().getName()));
                }
            }
        } else {
            throw new SigletError("Processor kind must be a String");
        }
    }

    private NodeChecker traceAggregatorChecker() {
        return requiredProperty(SigletItem::setConfig, "config",
                strictObject(TraceAggregatorConfig::new,
                        property(TraceAggregatorConfig::setTimeoutMillis,
                                "timeout-millis", false, anyNumberChecker()),
                        property(TraceAggregatorConfig::setInactiveTimeoutMillis,
                                "inactive-timeout-millis", false, anyNumberChecker()),
                        property(TraceAggregatorConfig::setCompletionExpression,
                                "completion-expression", false, text())));
    }
}

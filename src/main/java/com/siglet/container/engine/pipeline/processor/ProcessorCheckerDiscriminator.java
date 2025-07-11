package com.siglet.container.engine.pipeline.processor;

import com.siglet.SigletError;
import com.siglet.parser.Node;
import com.siglet.parser.NodeChecker;
import com.siglet.parser.SchemaValidationError;
import com.siglet.container.config.raw.ProcessorKind;
import com.siglet.parser.node.ObjectNode;
import com.siglet.parser.schema.DynamicCheckerDiscriminator;
import com.siglet.parser.schema.SingleSchemaValidationError;

public class ProcessorCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final ProcessorTypeRegistry processorTypeRegistry;

    public ProcessorCheckerDiscriminator(ProcessorTypeRegistry processorTypeRegistry) {
        this.processorTypeRegistry = processorTypeRegistry;
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
        if (kind.getValue() instanceof ProcessorKind kindValue) {
            if (kindValue == ProcessorKind.TRACE_AGGREGATOR) {
                throw new SigletError("Not implemented yet!");
            } else {
                Node type = objectNode.get("type");
                if (type == null) {
                    throw new SingleSchemaValidationError(node.getLocation(), "must have a type property!");
                }
                return getNodeChecker(node, type);
            }
        } else {
            throw new SigletError("Processor kind must be a String");
        }
    }

    private NodeChecker getNodeChecker(Node node, Node type) {
        if (type.getValue() instanceof String typeValue) {
            ProcessorType processorType = processorTypeRegistry.get(typeValue);
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

}

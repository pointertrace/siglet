package io.github.pointertrace.siglet.impl.engine.pipeline.processor;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.ConfigCheckFactory;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.schema.DynamicCheckerDiscriminator;
import io.github.pointertrace.siglet.parser.schema.SingleSchemaValidationError;

import java.util.HashSet;
import java.util.Set;

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
        Set<String> propertyNames = getAvailableProperties(objectNode);
        propertyNames.retainAll(processorTypeRegistry.getProcessorTypesNames());
        if (propertyNames.size() != 1) {
            Set<String> availableProcessorProperties = getAvailableProperties(objectNode);
            if (availableProcessorProperties.size() == 1) {
                throw new SigletError(String.format("Processor type %s at %s should be one of: %s",
                        availableProcessorProperties.toArray()[0].toString(),  node.getLocation(), String.join(", ",
                        processorTypeRegistry.getProcessorTypesNames())));

            } else {
                throw new SigletError(String.format("Expecting processor at %s to have one of: %s but available are: %s",
                        node.getLocation(), String.join(", ", processorTypeRegistry.getProcessorTypesNames()),
                        String.join(", ", getAvailableProperties(objectNode))));
            }
        }
        String processorTypeName = propertyNames.iterator().next();
        ProcessorType processorType = processorTypeRegistry.get(processorTypeName);
        if (processorType == null) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("could not find [%s] as siglet type", processorTypeName));
        }
        return processorType.getConfigDefinition().getChecker();
    }

    private Set<String> getAvailableProperties(ObjectNode objectNode) {
        Set<String> propertyNames = new HashSet<>(objectNode.getPropertyNames());
        propertyNames.removeAll(ConfigCheckFactory.getProcessorProperties());
        return propertyNames;
    }

}

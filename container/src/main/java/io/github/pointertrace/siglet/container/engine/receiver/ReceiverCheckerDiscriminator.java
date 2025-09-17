package io.github.pointertrace.siglet.container.engine.receiver;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigCheckFactory;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorType;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.schema.DynamicCheckerDiscriminator;
import io.github.pointertrace.siglet.parser.schema.SingleSchemaValidationError;

import java.util.HashSet;
import java.util.Set;

public class ReceiverCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final ReceiverTypeRegistry receiverTypeRegistry;

    public ReceiverCheckerDiscriminator(ReceiverTypeRegistry receiverTypeRegistry) {
        this.receiverTypeRegistry = receiverTypeRegistry;
    }

    @Override
    public NodeChecker getChecker(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "must be an object!");
        }
        Set<String> propertyNames = getAvailableProperties(objectNode);
        propertyNames.retainAll(receiverTypeRegistry.getReceiverTypesNames());
        if (propertyNames.size() != 1) {
            throw new SigletError(String.format("Expecting receiver at %s to have one of: %s but available are: %s",
                    node.getLocation(), String.join(", ", receiverTypeRegistry.getReceiverTypesNames()),
                    String.join(", ", getAvailableProperties(objectNode))));
        }
        String receiverTypeName = propertyNames.iterator().next();
        ReceiverType receiverType = receiverTypeRegistry.get(receiverTypeName);
        if (receiverType == null) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("could not find [%s] as receiver type", receiverTypeName));
        }
        return receiverType.getConfigDefinition().getChecker();
    }

    private Set<String> getAvailableProperties(ObjectNode objectNode) {
        Set<String> propertyNames = new HashSet<>(objectNode.getPropertyNames());
        propertyNames.removeAll(ConfigCheckFactory.getReceiverProperties());
        return propertyNames;
    }

}

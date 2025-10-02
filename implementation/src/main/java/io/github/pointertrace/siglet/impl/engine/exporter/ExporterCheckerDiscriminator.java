package io.github.pointertrace.siglet.impl.engine.exporter;

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

public class ExporterCheckerDiscriminator implements DynamicCheckerDiscriminator {

    private final ExporterTypeRegistry exporterTypeRegistry;

    public ExporterCheckerDiscriminator(ExporterTypeRegistry exporterTypeRegistry) {
        this.exporterTypeRegistry = exporterTypeRegistry;
    }

    @Override
    public NodeChecker getChecker(Node node) throws SchemaValidationError {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new SingleSchemaValidationError(node.getLocation(), "must be an object!");
        }
        Set<String> propertyNames = getAvailableProperties(objectNode);
        propertyNames.retainAll(exporterTypeRegistry.getExporterTypesNames());
        if (propertyNames.size() != 1) {
            throw new SigletError(String.format("Expecting receiver at %s to have one of: %s but available are: %s",
                    node.getLocation(), String.join(", ", exporterTypeRegistry.getExporterTypesNames()),
                    String.join(", ", getAvailableProperties(objectNode))));
        }
        String receiverTypeName = propertyNames.iterator().next();
        ExporterType receiverType = exporterTypeRegistry.get(receiverTypeName);
        if (receiverType == null) {
            throw new SingleSchemaValidationError(node.getLocation(),
                    String.format("could not find [%s] as receiver type", receiverTypeName));
        }
        return receiverType.getConfigDefinition().getChecker();
    }

    private Set<String> getAvailableProperties(ObjectNode objectNode) {
        Set<String> propertyNames = new HashSet<>(objectNode.getPropertyNames());
        propertyNames.removeAll(ConfigCheckFactory.getExporterProperties());
        return propertyNames;
    }

}

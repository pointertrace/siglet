package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.*;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private final Map<String, BaseNode> nodeRegistry = new HashMap<>();

    public void addDescriptor(BaseDescriptor descriptor) {
        switch (descriptor) {
            case ReceiverDescriptor receiverDescriptor -> nodeRegistry.put(receiverDescriptor.getName().getValue(),
                    new ReceiverNode(receiverDescriptor));

            case ExporterDescriptor exporterDescriptor -> nodeRegistry.put(exporterDescriptor.getName().getValue(),
                    new ExporterNode(exporterDescriptor));

            case PipelineDescriptor pipelineDescriptor -> nodeRegistry.put(pipelineDescriptor.getName().getValue(),
                    new PipelineNode(pipelineDescriptor));

            case ProcessorDescriptor siglet -> nodeRegistry.put(siglet.getName().getValue(),
                    new ProcessorNode(siglet));

            default -> throw new SigletError("Could not add config item type " + descriptor.getClass().getName());
        }
    }

    public List<BaseNode> getNodesByName(List<String> names) {
        return names.stream()
                .map(this::translateNodeName)
                .map(this::getNodeByName)
                .toList();
    }

    <T extends BaseNode> List<T> getNodesByNameAndType(List<String> names, Class<T> nodeType) {
        List<BaseNode> nodesByName = getNodesByName(names);
        nodesByName.forEach(node -> {
            if (!nodeType.isAssignableFrom(node.getClass())) {
                throw new SigletError(String.format("Node named [%s] is %s and should be %s", node.getName(),
                        node.getClass().getSimpleName(), nodeType.getSimpleName()));
            }
        });
        return nodesByName.stream()
                .map(nodeType::cast)
                .toList();
    }

    public BaseNode getNodeByName(String name) {
        String translatedName = translateNodeName(name);
        if (!nodeRegistry.containsKey(translatedName)) {
            throw new SigletError(String.format("Could not find any node named [%s]", name));
        }
        return nodeRegistry.get(translateNodeName(name));
    }

    public <T extends BaseNode> T getNodeByNameAndType(String name, Class<T> nodeType) {
        BaseNode node = getNodeByName(name);
        if (!nodeType.isAssignableFrom(node.getClass())) {
            throw new SigletError(String.format("Node named [%s] is %s and should be %s", name,
                    node.getClass().getSimpleName(), nodeType.getSimpleName()));
        }
        return nodeType.cast(node);
    }

    public void connect() {

        nodeRegistry.values().forEach(node -> {
            switch (node) {

                case ProcessorNode processorNode -> {
                    processorNode.setTo(getNodesByName(processorNode.getDescription().getTo().stream().map(StringValue::getValue).toList()));
                    processorNode.setPipeline(getNodeByNameAndType(processorNode.getDescription().getPipelineName(), PipelineNode.class));
                }
                case PipelineNode pipelineNode -> {
                    pipelineNode.getFrom().add(getNodeByNameAndType(pipelineNode.getDescription().getFrom().getValue(), ReceiverNode.class));
                    pipelineNode.getStart().addAll(getNodesByNameAndType(pipelineNode.getDescription().getStart().stream().map(StringValue::getValue).toList(),
                            ProcessorNode.class));
                }
                case ReceiverNode receiverNode -> {
                    receiverNode.getTo().addAll(nodeRegistry.values().stream()
                            .filter(PipelineNode.class::isInstance)
                            .map(PipelineNode.class::cast)
                            .filter(p -> p.getDescription().getFrom().getValue().equals(receiverNode.getName()))
                            .map(BaseNode::getName)
                            .map(name -> getNodeByNameAndType(name, PipelineNode.class))
                            .toList());
                }
                case ExporterNode exporterNode -> exporterNode.getFrom().addAll(nodeRegistry.values().stream()
                        .filter(ProcessorNode.class::isInstance)
                        .map(ProcessorNode.class::cast)
                        .filter(s -> s.getDescription().getTo().stream().map(StringValue::getValue).toList().contains(exporterNode.getName()))
                        .map(BaseNode::getName)
                        .map(name -> getNodeByNameAndType(name, ProcessorNode.class))
                        .toList());
            }

        });
    }

    private String translateNodeName(String baseNodeName) {
        return baseNodeName.contains(":") ?
                baseNodeName.substring(baseNodeName.indexOf(':') + 1) :
                baseNodeName;
    }


    public Collection<BaseNode> getNodeRegistry() {
        return nodeRegistry.values();
    }

}

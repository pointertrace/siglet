package io.github.pointertrace.siglet.container.config.graph;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private final Map<String, BaseNode> nodeRegistry = new HashMap<>();

    public void addItem(BaseConfig config) {
        switch (config) {
            case ReceiverConfig receiver -> nodeRegistry.put(receiver.getName(),
                    new ReceiverNode(receiver));

            case ExporterConfig exporter -> nodeRegistry.put(exporter.getName(),
                    new ExporterNode(exporter));

            case PipelineConfig pipeline -> nodeRegistry.put(pipeline.getName(),
                    new PipelineNode(pipeline));

            case ProcessorConfig siglet -> nodeRegistry.put(siglet.getName(),
                    new ProcessorNode(siglet));

            default -> throw new SigletError("Could not add config item type " + config.getClass().getName());
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
                    processorNode.setTo(getNodesByName(processorNode.getConfig().getToNames()));
                    processorNode.setPipeline(getNodeByNameAndType(processorNode.getConfig().getPipeline(), PipelineNode.class));
                }
                case PipelineNode pipelineNode -> {
                    pipelineNode.getFrom().add(getNodeByNameAndType(pipelineNode.getConfig().getFrom(), ReceiverNode.class));
                    pipelineNode.getStart().addAll(getNodesByNameAndType(pipelineNode.getConfig().getStartNames(),
                            ProcessorNode.class));
                }
                case ReceiverNode receiverNode -> {
                    receiverNode.getTo().addAll(nodeRegistry.values().stream()
                            .filter(PipelineNode.class::isInstance)
                            .map(PipelineNode.class::cast)
                            .filter(p -> p.getConfig().getFrom().equals(receiverNode.getName()))
                            .map(BaseNode::getName)
                            .map(name -> getNodeByNameAndType(name, PipelineNode.class))
                            .toList());
                }
                case ExporterNode exporterNode -> exporterNode.getFrom().addAll(nodeRegistry.values().stream()
                        .filter(ProcessorNode.class::isInstance)
                        .map(ProcessorNode.class::cast)
                        .filter(s -> s.getConfig().getToNames().contains(exporterNode.getName()))
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

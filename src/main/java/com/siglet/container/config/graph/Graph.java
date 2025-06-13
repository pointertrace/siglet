package com.siglet.container.config.graph;

import com.siglet.SigletError;
import com.siglet.container.config.raw.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Graph {

    private final Map<String, BaseNode> nodes = new HashMap<>();

    public void addItem(BaseConfig config) {
        switch (config) {
            case ReceiverConfig receiver -> nodes.put(receiver.getName(),
                    new ReceiverNode(receiver));

            case ExporterConfig exporter -> nodes.put(exporter.getName(),
                    new ExporterNode(exporter));

            case PipelineConfig pipeline -> nodes.put(pipeline.getName(),
                    new PipelineNode(pipeline));

            case ProcessorConfig siglet -> nodes.put(siglet.getName(),
                    new ProcessorNode(siglet));

            default -> throw new SigletError("Could not add config item type " + config.getClass().getName());
        }
    }


    List<BaseNode> getNodesByName(List<String> names) {
        return names.stream()
                .map(this::getNodeByName)
                .toList();
    }

    <T extends BaseNode> List<T> getNodesByNameAndType(List<String> names, Class<T> nodeType) {
        List<BaseNode> nodes = getNodesByName(names);
        nodes.forEach(node -> {
            if (!nodeType.isAssignableFrom(node.getClass())) {
                throw new SigletError(String.format("Node named [%s] is %s and should be %s", node.getName(),
                        node.getClass().getSimpleName(), nodeType.getSimpleName()));
            }
        });
        return nodes.stream()
                .map(nodeType::cast)
                .toList();
    }

    BaseNode getNodeByName(String name) {
        if (!nodes.containsKey(name)) {
            throw new SigletError(String.format("Could not find any node named [%s]", name));
        }
        return nodes.get(name);
    }

    <T extends BaseNode> T getNodeByNameAndType(String name, Class<T> nodeType) {
        BaseNode node = getNodeByName(name);
        if (!nodeType.isAssignableFrom(node.getClass())) {
            throw new SigletError(String.format("Node named [%s] is %s and should be %s", name,
                    node.getClass().getSimpleName(), nodeType.getSimpleName()));
        }
        return nodeType.cast(node);
    }

    public void connect() {

        nodes.values().forEach(node -> {
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

                case ReceiverNode receiverNode -> receiverNode.getTo().addAll(nodes.values().stream()
                        .filter(PipelineNode.class::isInstance)
                        .map(PipelineNode.class::cast)
                        .filter(p -> p.getConfig().getFrom().equals(receiverNode.getName()))
                        .map(BaseNode::getName)
                        .map(name -> getNodeByNameAndType(name, PipelineNode.class))
                        .toList());


                case ExporterNode exporterNode -> exporterNode.getFrom().addAll(nodes.values().stream()
                        .filter(ProcessorNode.class::isInstance)
                        .map(ProcessorNode.class::cast)
                        .filter(s -> s.getConfig().getToNames().contains(exporterNode.getName()))
                        .map(BaseNode::getName)
                        .map(name -> getNodeByNameAndType(name, ProcessorNode.class))
                        .toList());

            }
        });
    }


    public Collection<BaseNode> getNodes() {
        return nodes.values();
    }

}

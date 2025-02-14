package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.*;
import com.siglet.config.item.repository.routecreator.RootRouteCreator;
import org.apache.camel.builder.RouteBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeRepository {

    private final Map<String, Node<?>> nodes = new HashMap<>();

    public void addItem(Item item) {
        switch (item) {
            case ReceiverItem receiver -> nodes.put(receiver.getName(),
                    new ReceiverNode(receiver, this));

            case ExporterItem exporter -> nodes.put(exporter.getName(),
                    new ExporterNode(exporter, this));

            case PipelineItem pipeline -> nodes.put(pipeline.getName(),
                    new PipelineNode(pipeline, this));

            case SigletItem siglet -> nodes.put(siglet.getName(),
                    new SigletNode(siglet, this));

            default -> throw new SigletError("Could not add config item type " + item.getClass().getName());
        }
    }


    List<Node<?>> getNodesByName(List<String> names) {
        return names.stream()
                .map(this::getNodeByName)
                .collect(Collectors.<Node<?>>toUnmodifiableList());
    }

    <T extends Node<?>> List<T> getNodesByNameAndType(List<String> names, Class<T> nodeType) {
        List<Node<?>> nodes = getNodesByName(names);
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

    Node<?> getNodeByName(String name) {
        if (!nodes.containsKey(name)) {
            throw new SigletError(String.format("Could not find any node named [%s]", name));
        }
        return nodes.get(name);
    }

    <T extends Node<?>> T getNodeByNameAndType(String name, Class<T> nodeType) {
        Node<?> node = getNodeByName(name);
        if (!nodeType.isAssignableFrom(node.getClass())) {
            throw new SigletError(String.format("Node named [%s] is %s and should be %s", name,
                    node.getClass().getSimpleName(), nodeType.getSimpleName()));
        }
        return nodeType.cast(node);
    }

    public void connect() {

        nodes.values().forEach(node -> {
            switch (node) {
                case SigletNode sigletNode -> {
                    sigletNode.setTo(getNodesByName(sigletNode.getItem().getToNames()));
                    sigletNode.setPipeline(getNodeByNameAndType(sigletNode.getItem().getPipeline(), PipelineNode.class));
                }
                case PipelineNode pipelineNode -> {
                    pipelineNode.getFrom().add(getNodeByNameAndType(pipelineNode.getItem().getFrom(), ReceiverNode.class));
                    pipelineNode.getStart().addAll(getNodesByNameAndType(pipelineNode.getItem().getStartNames(),
                            SigletNode.class));
                }

                case ReceiverNode receiverNode -> receiverNode.getTo().addAll(nodes.values().stream()
                        .filter(PipelineNode.class::isInstance)
                        .map(PipelineNode.class::cast)
                        .filter(p -> p.getItem().getFrom().equals(receiverNode.getName()))
                        .map(Node::getName)
                        .map(name -> getNodeByNameAndType(name, PipelineNode.class))
                        .toList());


                case ExporterNode exporterNode -> exporterNode.getFrom().addAll(nodes.values().stream()
                        .filter(SigletNode.class::isInstance)
                        .map(SigletNode.class::cast)
                        .filter(s -> s.getItem().getToNames().contains(exporterNode.getName()))
                        .map(Node::getName)
                        .map(name -> getNodeByNameAndType(name, SigletNode.class))
                        .toList());

            }
        });
    }

    public RouteBuilder createRouteBuilder() {
        RootRouteCreator routeCreator = new RootRouteCreator();

        nodes.values().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .forEach(receiverNode -> {
                    receiverNode.createRoute(routeCreator);
                });

        return routeCreator.getRouteBuilder();
    }


    public Collection<Node<?>> getNodes() {
        return nodes.values();
    }

}

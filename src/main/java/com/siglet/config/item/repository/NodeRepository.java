package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.*;
import com.siglet.config.item.repository.routecreator.RootRouteCreator;
import com.siglet.spanlet.traceaggregator.TraceAggregatorItem;
import org.apache.camel.builder.RouteBuilder;

import java.util.*;

public class NodeRepository {

    private final Map<String, Node<?>> repository = new HashMap<>();

    public void addItem(Item item) {
        switch (item) {
            case ReceiverItem receiverItem -> repository.put(receiverItem.getName().getValue(),
                    new ReceiverNode(receiverItem.getName().getValue(), receiverItem));
            case ExporterItem exporterItem -> repository.put(exporterItem.getName().getValue(),
                    new ExporterNode(exporterItem.getName().getValue(), exporterItem));
            case TracePipelineItem tracePipelineItem -> repository.put(tracePipelineItem.getName().getValue(),
                    new PipelineNode(tracePipelineItem.getName().getValue(),
                            tracePipelineItem));
            case SpanletItem spanletItem -> repository.put(spanletItem.getName().getValue(), new SpanletNode(
                    spanletItem.getName().getValue(), spanletItem));
            case TraceAggregatorItem traceAggregatorItem -> repository.put(traceAggregatorItem.getName().getValue(),
                    new TraceAggregatorNode(traceAggregatorItem.getName().getValue(), traceAggregatorItem));
            case TraceletItem traceletItem -> repository.put(traceletItem.getName().getValue(),
                    new TraceletNode(traceletItem.getName().getValue(), traceletItem));
            default -> throw new SigletError("Could not add config item type " + item.getClass().getName());
        }
    }

    public List<ReceiverNode> getReceiverNodesFromNames(List<ValueItem<String>> names) {
        List<ReceiverNode> nodes = new ArrayList<>();
        for (String name : names.stream().map(ValueItem::getValue).toList()) {
            Node<?> node = repository.get(name);
            if (node == null) {
                throw new SigletError("Could not find any config item named [" + name + "]");
            } else {
                if (!(node instanceof ReceiverNode receiverNode)) {
                    throw new SigletError("Node [" + name + "] is a " + node.getClass().getName() + "not a ReceiverNode");
                } else {
                    nodes.add(receiverNode);
                }
            }
        }
        return nodes;
    }

    List<Node<?>> getNodesFromNames(List<String> names) {
        List<Node<?>> nodes = new ArrayList<>();
        for (String name : names) {
            Node<?> node = repository.get(name);
            if (node == null) {
                throw new SigletError("Could not find any config item named [" + name + "]");
            }
            nodes.add(node);
        }
        return nodes;
    }

    List<ProcessorNode<?>> getProcessorNodesFromNames(List<String> names) {
        List<ProcessorNode<?>> nodes = new ArrayList<>();
        for (String name : names) {
            Node<?> node = repository.get(name);
            if (node == null) {
                throw new SigletError("Could not find any config item named [" + name + "]");
            } else {
                if (!(node instanceof ProcessorNode<?> processorNode)) {
                    throw new SigletError("Node [" + name + "] is a " + node.getClass().getName() + "not a ProcessorNode");
                } else {
                    nodes.add(processorNode);
                }
            }
        }
        return nodes;
    }

    public void connect() {

        repository.values().forEach(node -> {
            switch (node) {
                case ProcessorNode<?> processorNode -> {
                    List<Node<?>> toNodes = getNodesFromNames(
                            processorNode.getItem().getTo().getValue().stream()
                                    .map(ValueItem::getValue)
                                    .toList());
                    toNodes.forEach(toNode -> {
                        if (toNode instanceof ExporterNode exporterNode) {
                            exporterNode.getFrom().add(processorNode);
                        }
                    });
                    processorNode.setTo(toNodes);

                    processorNode.setPipeline((PipelineNode) repository.get(processorNode.getItem().getPipeline()));

                }
                case PipelineNode pipelineNode -> {
                    List<ReceiverNode> fromNodes = getReceiverNodesFromNames(pipelineNode.getItem().getFrom());
                    fromNodes.forEach(fromNode -> {
                        if (fromNode instanceof ReceiverNode receiverNode) {
                            receiverNode.getTo().add(pipelineNode);
                        }
                    });
                    pipelineNode.setFrom(fromNodes);

                    List<ProcessorNode<?>> startNodes = getProcessorNodesFromNames(
                            pipelineNode.getItem().getStart().stream().map(ValueItem::getValue).toList());
                    pipelineNode.setStart(startNodes);
                }
                case ReceiverNode receiverNode -> {
                }
                case ExporterNode exporterNode -> {
                }
                default -> throw new IllegalStateException("not yet implemented!");
            }
        });
    }

    public RouteBuilder createRouteBuilder() {
        RootRouteCreator routeCreator = new RootRouteCreator();

        repository.values().stream()
                .filter(ReceiverNode.class::isInstance)
                .map(ReceiverNode.class::cast)
                .forEach(receiverNode -> {
                    receiverNode.createRoute(routeCreator);
                });

        return routeCreator.getRouteBuilder();
    }


    public Collection<Node<?>> getNodes() {
        return repository.values();
    }

}

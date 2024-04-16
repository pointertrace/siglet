package com.siglet.config.item.repository;

import com.siglet.SigletError;
import com.siglet.config.item.*;
import com.siglet.config.item.repository.routecreator.RootRouteCreator;
import com.siglet.config.item.repository.routecreator.RouteCreator;
import com.siglet.spanlet.GroovyPropertySetter;
import com.siglet.spanlet.filter.FilterConfig;
import com.siglet.spanlet.filter.GroovyPredicate;
import com.siglet.spanlet.processor.GroovyProcessor;
import com.siglet.spanlet.processor.ProcessorConfig;
import com.siglet.spanlet.router.RouterConfig;
import com.siglet.spanlet.router.Route;
import org.apache.camel.builder.RouteBuilder;

import java.util.*;

public class NodeRepository {

    private final Map<String, Node<?>> repository = new HashMap<>();

    public void addItem(Item item) {
        switch (item) {
            case ReceiverItem receiverItem -> repository.put(receiverItem.getName(),
                    new ReceiverNode(receiverItem.getName(), receiverItem));
            case ExporterItem exporterItem -> repository.put(exporterItem.getName(),
                    new ExporterNode(exporterItem.getName(), exporterItem));
            case TracePipelineItem tracePipelineItem -> repository.put(tracePipelineItem.getName(),
                    new PipelineNode(tracePipelineItem.getName(), tracePipelineItem));
            case SpanletItem spanletItem ->
                    repository.put(spanletItem.getName(), new SpanletNode(spanletItem.getName(), spanletItem));
            default -> throw new SigletError("Could not add config item type " + item.getClass().getName());
        }
    }

    public List<Node<?>> getFromNames(List<String> names) {
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

    public <T extends Node<?>> List<T> getFromNames(Class<T> nodeType, List<String> names) {
        List<T> nodes = new ArrayList<>();
        for (String name : names) {
            Node<?> node = repository.get(name);
            if (node == null) {
                throw new SigletError("Could not find any config item named [" + name + "]");
            } else if (!node.getClass().isAssignableFrom(nodeType)) {
                throw new SigletError(String.format("Expecting node %s to be of type %s but its type is %s!", name,
                        nodeType.getName(), node.getClass().getName()));
            }
            nodes.add((T) node);
        }
        return nodes;
    }

    public void connect() {
        repository.values().forEach(node -> {
            switch (node) {
                case SpanletNode spanletNode -> {
                    List<Node<?>> toNodes = getFromNames(spanletNode.getItem().getTo());
                    toNodes.forEach(toNode -> {
                        if (toNode instanceof ExporterNode exporterNode) {
                            exporterNode.getFrom().add(spanletNode);
                        }
                        // e se não for???
                    });
                    spanletNode.setTo(toNodes);

                    spanletNode.setPipeline((PipelineNode) repository.get(spanletNode.getItem().getPipeline()));

                }
                case PipelineNode pipelineNode -> {
                    List<ReceiverNode> fromNodes = getFromNames(ReceiverNode.class, pipelineNode.getItem().getFrom());
                    fromNodes.forEach(fromNode -> {
                        if (fromNode instanceof ReceiverNode receiverNode) {
                            receiverNode.getTo().add(pipelineNode);
                        }
                        // e se não for
                    });
                    pipelineNode.setFrom(fromNodes);

                    List<SpanletNode> startNodes = getFromNames(SpanletNode.class, pipelineNode.getItem().getStart());
                    pipelineNode.setStart(startNodes);

                }
                case ReceiverNode receiverNode -> {
                    System.out.println("nothing to do!");
                }
                case ExporterNode exporterNode -> {
                    System.out.println("nothing to do!");
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
                    grpcReceiver(receiverNode, routeCreator);
                });
        return routeCreator.getRouteBuilder();
    }

    public void next(Node<?> node, RouteCreator routeCreator) {
        switch (node) {
            case SpanletNode spanletNode -> {
                spanlet(spanletNode, routeCreator);
            }
            case ExporterNode exporterNode -> {
                grpcExporter(exporterNode, routeCreator);
            }
            case PipelineNode pipelineNode -> {
                pipeline(pipelineNode, routeCreator);

            }
            default -> throw new IllegalStateException("not yet implemented!");
        }
    }

    public void pipeline(PipelineNode pipelineNode, RouteCreator routeCreator) {
        if (pipelineNode.getStart().size() == 1) {
            next(pipelineNode.getStart().getFirst(), routeCreator);
        } else {
            RouteCreator multicast = routeCreator.startMulticast();
            for (SpanletNode node : pipelineNode.getStart()) {
                next(node, multicast);
            }
            multicast.endMulticast();
        }
    }

    public void grpcReceiver(ReceiverNode receiverNode, RouteCreator routeCreator) {
        if (receiverNode.getTo().size() == 1) {
            next(receiverNode.getTo().getFirst(), routeCreator.addReceiver(receiverNode.getUri()));
        } else {
            throw new IllegalStateException("not yet implemented!");
        }
    }

    public void grpcExporter(ExporterNode exporterNode, RouteCreator routeCreator) {
        routeCreator.addExporter(exporterNode.getUri());
    }

    public void spanlet(SpanletNode spanletNode, RouteCreator routeCreator) {
        switch (spanletNode.getItem().getType()) {
            case "processor":
                Object config = spanletNode.getItem().getConfig();
                if (!(config instanceof ProcessorConfig processorConfig)) {
                    throw new SigletError("");
                }
                if (spanletNode.getTo().size() == 1) {
                    next(spanletNode.getTo().getFirst(), routeCreator.
                            addProcessor(new GroovyProcessor(processorConfig.getAction(), GroovyPropertySetter.span)));
                } else {
                    RouteCreator multicast = routeCreator.addProcessor(
                            new GroovyProcessor(processorConfig.getAction(),GroovyPropertySetter.span)).startMulticast();
                    for (Node<?> node : spanletNode.getTo()) {
                        next(node, multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "filter":
                config = spanletNode.getItem().getConfig();
                if (!(config instanceof FilterConfig filterConfig)) {
                    throw new SigletError("");
                }
                if (spanletNode.getTo().size() == 1) {
                    next(spanletNode.getTo().getFirst(), routeCreator.addFilter(
                            new GroovyPredicate(filterConfig.getExpression(), GroovyPropertySetter.span)));
                } else {
                    RouteCreator multicast = routeCreator.addFilter(
                                    new GroovyPredicate(filterConfig.getExpression(),GroovyPropertySetter.span))
                            .startMulticast();
                    for (Node<?> node : spanletNode.getTo()) {
                        next(node, multicast);
                    }
                    multicast.endMulticast();
                }
                break;
            case "router":
                config = spanletNode.getItem().getConfig();
                if (!(config instanceof RouterConfig routerConfig)) {
                    throw new SigletError("");
                }
                RouteCreator choice = routeCreator.startChoice();
                for (Route route : routerConfig.getRoutes()) {
                    next(getToFromName(spanletNode.getTo(), route.getTo()), choice.addChoice(
                            new GroovyPredicate(route.getExpression(), GroovyPropertySetter.span)));
                }
                next(getToFromName(spanletNode.getTo(), routerConfig.getDefaultRoute()), choice.endChoice());
                break;
            default:
                throw new IllegalStateException("not yet implemented");
        }
    }

    public Node<?> getToFromName(List<Node<?>> to, String name) {
        for (Node<?> node : to) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        throw new SigletError("Could not find any config item named [" + name + "]");

    }


    public Collection<Node<?>> getNodes() {
        return repository.values();
    }

}

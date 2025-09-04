package io.github.pointertrace.siglet.container.config.graph.validator;

import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.graph.*;

import java.util.*;
import java.util.stream.Collectors;

public class CircularReferenceValidator implements GraphValidator {

    @Override
    public void validate(Graph graph) {

        Map<String, BaseNode> visitedNodes = new HashMap<>();
        Set<String> circularReferences = graph.getNodeRegistry().stream()
                .filter(PipelineNode.class::isInstance)
                .map(PipelineNode.class::cast)
                .flatMap(pipelineNode -> checkCircularReference(pipelineNode, visitedNodes, "").stream())
                .collect(Collectors.toSet());

        if (!circularReferences.isEmpty()) {
            throw new SigletError(circularReferences.stream()
                    .map(circularReference -> Describable.prefix(2) + circularReference)
                    .collect(Collectors.joining("\n", "There are circular references:\n",
                            "")));
        }
    }

    private List<String> checkCircularReference(BaseNode node, Map<String, BaseNode> visitedNodes, String path) {

        Map<String, BaseNode> visitedNodesCopy = new HashMap<>(visitedNodes);
        String localPath = addToPath(node, path);
        List<String> circularReferences = new ArrayList<>();

        if (visitedNodesCopy.containsKey(node.getName())) {
            circularReferences.add(localPath);
            return circularReferences;
        }
        visitedNodes.put(node.getName(), node);
        switch (node) {
            case ProcessorNode processorNode -> {
                for (BaseNode toNode : processorNode.getTo()) {
                    circularReferences.addAll(checkCircularReference(toNode, visitedNodes, localPath));
                }
            }
            case PipelineNode pipelineNode -> {
                for (BaseNode toNode : pipelineNode.getStart()) {
                    circularReferences.addAll(checkCircularReference(toNode, visitedNodes, localPath));
                }
            }
            case ExporterNode exporterNode -> {
            }
            case ReceiverNode receiverNode -> {
            }
        }
        return circularReferences;
    }

    private String addToPath(BaseNode node, String path) {
        switch (node) {
            case PipelineNode pipelineNode -> {
                return path + String.format("-> pipeline [%s] at %s", pipelineNode.getName(),
                        pipelineNode.getConfig().getLocation());
            }
            case ProcessorNode processorNode -> {
                return path + String.format("-> processor [%s] at %s", processorNode.getName(),
                        processorNode.getConfig().getLocation());
            }
            case ExporterNode exporterNode -> {
                return path;
            }
            default -> throw new SigletError("Unexpected value: " + node);
        }

    }

}

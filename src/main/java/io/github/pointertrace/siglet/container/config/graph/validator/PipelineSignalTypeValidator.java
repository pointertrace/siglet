package io.github.pointertrace.siglet.container.config.graph.validator;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.graph.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PipelineSignalTypeValidator implements GraphValidator {


    @Override
    public void validate(Graph graph) {

        graph.getNodeRegistry().stream()
                .filter(PipelineNode.class::isInstance)
                .map(PipelineNode.class::cast)
                .forEach(this::validatePipeline);


    }

    private void validatePipeline(PipelineNode pipelineNode) {
        Map<SignalType, List<ProcessorNode>> nodesSignalTypes = getAllNodes(pipelineNode).stream().
                collect(Collectors
                        .groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        if (nodesSignalTypes.size() > 1) {
            String nodeNames = nodesSignalTypes.entrySet().stream()
                    .map(this::description)
                    .collect(Collectors.joining("\n"));
            throw new SigletError(String.format("Pipeline [%s] at %s has multiple signal types:\n%s",
                    pipelineNode.getName(), pipelineNode.getConfig().getLocation(), nodeNames));
        }
    }

    public String description(Map.Entry<SignalType, List<ProcessorNode>> nodesSignalTypes) {
        return String.format("%s%s: %s", Describable.prefix(2),
                nodesSignalTypes.getKey().name(),
                nodesSignalTypes.getValue().stream()
                        .map(node -> String.format("[%s] at %s", node.getName(), node.getConfig().getLocation()))
                        .collect(Collectors.joining(", ")));
    }


    private List<Map.Entry<SignalType, ProcessorNode>> getAllNodes(BaseNode node) {
        switch (node) {
            case PipelineNode pipelineNode -> {
                return pipelineNode.getStart().stream()
                        .flatMap(startNode -> getAllNodes(startNode).stream())
                        .toList();
            }
            case ProcessorNode processorNode -> {
                if (processorNode.getTo() == null) {
                    return List.of(new AbstractMap.SimpleImmutableEntry<>(processorNode.getSignal(), processorNode));
                } else {
                    ArrayList<Map.Entry<SignalType, ProcessorNode>> nodes = new ArrayList<>();
                    nodes.add(new AbstractMap.SimpleImmutableEntry<>(processorNode.getSignal(), processorNode));
                    nodes.addAll(processorNode.getTo().stream()
                            .flatMap(toNode -> getAllNodes(toNode).stream())
                            .toList());
                    return nodes;
                }
            }
            case ExporterNode exporterNode -> {
                return List.of();
            }
            default -> throw new SigletError("Unexpected value: " + node);
        }

    }
}

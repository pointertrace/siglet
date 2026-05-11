package io.github.pointertrace.siglet.impl.config.graph;

import io.github.pointertrace.siglet.impl.config.Config;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;

public class GraphFactory {

    public Graph create(YamlDescriptor yamlDescriptor) {
        Graph graph = new Graph();

        yamlDescriptor.getReceivers().forEach(graph::addDescriptor);
        yamlDescriptor.getExporters().forEach(graph::addDescriptor);
        yamlDescriptor.getPipelines().forEach(graph::addDescriptor);
        yamlDescriptor.getPipelines().stream()
                .flatMap(pipelineDescriptor -> pipelineDescriptor.getProcessors().stream())
                .forEach(graph::addDescriptor);

        graph.connect();

        return graph;
    }
}

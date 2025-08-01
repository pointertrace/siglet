package io.github.pointertrace.siglet.container.config.graph;

import io.github.pointertrace.siglet.container.config.Config;

public class GraphFactory {

    public Graph create(Config config) {
        Graph graph = new Graph();

        config.getRawConfig().getReceivers().forEach(graph::addItem);
        config.getRawConfig().getExporters().forEach(graph::addItem);
        config.getRawConfig().getPipelines().forEach(graph::addItem);
        config.getRawConfig().getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .forEach(graph::addItem);

        graph.connect();

        return graph;
    }
}

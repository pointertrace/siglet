package com.siglet.container.config.graph.validator;

import com.siglet.container.config.graph.Graph;

public class OrphanExporterValidator implements GraphValidator {

    @Override
    public void validate(Graph graph) {
//        Set<String> orphanExporters = nodeRepository.getNodes().stream()
//                .filter(ExporterNode.class::isInstance)
//                .map(Node::getName)
//                .collect(Collectors.toSet());
//
//        if (orphanExporters.size() == 1) {
//            throw new SigletError("Exporter [" + new ArrayList<>(orphanExporters).getFirst() + "] is not being used!");
//        } else if (orphanExporters.size() > 1) {
//            throw new SigletError("Exporters [" + String.join(",", orphanExporters) + "] are not being used!");
//        }

    }
}

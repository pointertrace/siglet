package com.siglet.config.item.repository.validator;

import com.siglet.SigletError;
import com.siglet.config.item.repository.ExporterNode;
import com.siglet.config.item.repository.Node;
import com.siglet.config.item.repository.NodeRepository;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class OrphanExporterValidator implements RepositoryValidator {

    @Override
    public void validate(NodeRepository nodeRepository) {
        Set<String> orphanExporters = nodeRepository.getNodes().stream()
                .filter(ExporterNode.class::isInstance)
                .map(Node::getName)
                .collect(Collectors.toSet());

        if (orphanExporters.size() == 1) {
            throw new SigletError("Exporter [" + new ArrayList<>(orphanExporters).getFirst() + "] is not being used!");
        } else if (orphanExporters.size() > 1) {
            throw new SigletError("Exporters [" + String.join(",", orphanExporters) + "] are not being used!");
        }

    }
}

package io.github.pointertrace.siglet.impl.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.raw.LocatedString;
import io.github.pointertrace.siglet.impl.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.impl.config.raw.RawConfig;
import io.github.pointertrace.siglet.parser.Describable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessorOrphanValidator implements RawConfigValidator {


    @Override
    public void validate(RawConfig config) {
        Set<String> destinations = new HashSet<>();

        destinations.addAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getStart().stream())
                .map(LocatedString::getValue)
                .collect(Collectors.toSet()));

        destinations.addAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .flatMap(processorConfig -> processorConfig.getTo().stream())
                .map(LocatedString::getValue)
                .map(destination -> destination.contains(":") ? destination.split(":")[1] : destination)
                .collect(Collectors.toSet()));


        Set<ProcessorConfig> orphanProcessors = config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .filter(processor -> !destinations.contains(processor.getName()))
                .collect(Collectors.toSet());

        if (!orphanProcessors.isEmpty()) {
            throw new SigletError(orphanProcessors.stream()
                    .map(exporter -> String.format("%s[%s] at %s", Describable.prefix(2),
                            exporter.getName(), exporter.getLocation()))
                    .collect(Collectors.joining("\n", "The following processors are orphaned:\n", "")));
        }
    }
}

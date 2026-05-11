package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessorOrphanValidator implements YamlDescriptorValidator {


    @Override
    public void validate(YamlDescriptor config) {
        Set<String> destinations = new HashSet<>();

        destinations.addAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getStart().stream())
                .map(StringValue::getValue)
                .collect(Collectors.toSet()));

        destinations.addAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .flatMap(processorConfig -> processorConfig.getTo().stream())
                .map(StringValue::getValue)
                .map(destination -> destination.contains(":") ? destination.split(":")[1] : destination)
                .collect(Collectors.toSet()));


        Set<ProcessorDescriptor> orphanProcessorDescriptors = config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .filter(processor -> !destinations.contains(processor.getName().getValue()))
                .collect(Collectors.toSet());

        if (!orphanProcessorDescriptors.isEmpty()) {
            throw new SigletError(orphanProcessorDescriptors.stream()
                    .map(exporter -> String.format("    [%s] at %s",
                            exporter.getName().getValue(),
                            exporter.getLocation().print())) // exporter location
                    .collect(Collectors.joining("\n", "The following processors are orphaned:\n", "")));
        }
    }
}

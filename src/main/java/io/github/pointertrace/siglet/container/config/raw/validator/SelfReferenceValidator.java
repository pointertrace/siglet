package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siget.parser.Describable;
import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.*;
import java.util.stream.Collectors;

public class SelfReferenceValidator implements RawConfigValidator {


    @Override
    public void validate(RawConfig config) {

        List<String> selfReferences = new ArrayList<>();

        selfReferences.addAll(config.getPipelines().stream()
                .map(this::validatePipeline)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        selfReferences.addAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .map(this::validateProcessor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        if (!selfReferences.isEmpty()) {
            throw new SigletError(selfReferences.stream()
                    .collect(Collectors.joining("\n", "The following items have a self reference:\n", "")));
        }

    }

    private String validateProcessor(ProcessorConfig processorConfig) {
        return processorConfig.getTo().stream()
                .map(LocatedString::getValue)
                .filter(to -> to.equals(processorConfig.getName()))
                .map(to -> String.format("%sprocessor [%s] at %s",
                        Describable.prefix(2), processorConfig.getName(), processorConfig.getLocation()))
                .findAny()
                .orElse(null);
    }

    private String validatePipeline(PipelineConfig pipelineConfig) {
        return pipelineConfig.getStart().stream()
                .map(LocatedString::getValue)
                .filter(start -> start.equals(pipelineConfig.getName()))
                .map(to -> String.format("%spipeline [%s] at %s",
                        Describable.prefix(2), pipelineConfig.getName(), pipelineConfig.getLocation()))
                .findAny()
                .orElse(null);
    }

}

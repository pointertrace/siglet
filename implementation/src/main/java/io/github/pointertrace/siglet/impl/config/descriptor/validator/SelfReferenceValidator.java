package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.PipelineDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ProcessorDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SelfReferenceValidator implements YamlDescriptorValidator {


    @Override
    public void validate(YamlDescriptor config) {

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

    private String validateProcessor(ProcessorDescriptor processorDescriptor) {
        return processorDescriptor.getTo().stream()
                .map(StringValue::getValue)
                .filter(to -> to.equals(processorDescriptor.getName().getValue()))
                .map(to -> String.format("    processor [%s] at %s",
                         processorDescriptor.getName().getValue(),
                        processorDescriptor.getLocation().print()))
                .findAny()
                .orElse(null);
    }

    private String validatePipeline(PipelineDescriptor pipelineDescriptorConfig) {
        return pipelineDescriptorConfig.getStart().stream()
                .map(StringValue::getValue)
                .filter(start -> start.equals(pipelineDescriptorConfig.getName().getValue()))
                .map(to -> String.format("    pipeline [%s] at %s",
                         pipelineDescriptorConfig.getName().getValue(),
                        pipelineDescriptorConfig.getLocation().print()))
                .findAny()
                .orElse(null);
    }

}

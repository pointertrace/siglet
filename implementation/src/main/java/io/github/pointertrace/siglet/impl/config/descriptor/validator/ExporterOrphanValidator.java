package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExporterOrphanValidator implements YamlDescriptorValidator {

    @Override
    public void validate(YamlDescriptor yamlDescriptor) {

        Map<String, BaseDescriptor> exporters = yamlDescriptor.getExporters().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity()));

        exporters.keySet().removeAll(yamlDescriptor.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .flatMap(processorConfig -> processorConfig.getTo().stream())
                .map(StringValue::getValue)
                .map(destination -> destination.contains(":") ? destination.split(":")[1] : destination)
                .collect(Collectors.toSet()));

        if (!exporters.isEmpty()) {
            throw new SigletError(exporters.values().stream()
                    .map(exporter -> String.format("    [%s] at %s",
                            exporter.getName().getValue(),
                            exporter.getLocation().print()))
                    .collect(Collectors.joining("\n", "The following exporters are orphaned:\n", "")));
        }
    }
}

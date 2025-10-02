package io.github.pointertrace.siglet.impl.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.raw.BaseConfig;
import io.github.pointertrace.siglet.impl.config.raw.LocatedString;
import io.github.pointertrace.siglet.impl.config.raw.RawConfig;
import io.github.pointertrace.siglet.parser.Describable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExporterOrphanValidator implements RawConfigValidator {

    @Override
    public void validate(RawConfig config) {

        Map<String, BaseConfig> exporters = config.getExporters().stream()
                .collect(Collectors.toMap(BaseConfig::getName, Function.identity()));

        exporters.keySet().removeAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .flatMap(processorConfig -> processorConfig.getTo().stream())
                .map(LocatedString::getValue)
                .map(destination -> destination.contains(":") ? destination.split(":")[1] : destination)
                .collect(Collectors.toSet()));


        if (!exporters.isEmpty()) {
            throw new SigletError(exporters.values().stream()
                    .map(exporter -> String.format("%s[%s] at %s", Describable.prefix(2),
                            exporter.getName(), exporter.getLocation()))
                    .collect(Collectors.joining("\n", "The following exporters are orphaned:\n", "")));
        }
    }
}

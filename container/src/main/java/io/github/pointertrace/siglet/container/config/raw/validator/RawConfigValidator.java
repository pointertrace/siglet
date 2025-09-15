package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface RawConfigValidator {


    void validate(RawConfig config);

    default String getItemType(BaseConfig item) {
        return switch (item) {
            case ReceiverConfig ignored -> "receiver";
            case ExporterConfig ignored -> "exporter";
            case PipelineConfig ignored -> "pipeline";
            case ProcessorConfig ignored -> "processor";
            default -> throw new SigletError("Unknown item type");
        };
    }




    default Map<String, BaseConfig> getUniqueNamedConfigItems(RawConfig config) {

        Map<String, BaseConfig> namedConfigItems = new HashMap<>();

        namedConfigItems.putAll(config.getReceivers().stream()
                .collect(Collectors.toMap(ReceiverConfig::getName, Function.identity())));

        namedConfigItems.putAll(config.getExporters().stream()
                .collect(Collectors.toMap(ExporterConfig::getName, Function.identity())));

        namedConfigItems.putAll(config.getPipelines().stream()
                .collect(Collectors.toMap(PipelineConfig::getName, Function.identity())));

        namedConfigItems.putAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .collect(Collectors.toMap(ProcessorConfig::getName, Function.identity())));

        return namedConfigItems;
    }

}

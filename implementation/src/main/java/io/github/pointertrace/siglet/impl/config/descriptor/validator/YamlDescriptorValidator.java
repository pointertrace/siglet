package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface YamlDescriptorValidator {


    void validate(YamlDescriptor config);

    default String getItemType(BaseDescriptor item) {
        return switch (item) {
            case ReceiverDescriptor ignored -> "receiver";
            case ExporterDescriptor ignored -> "exporter";
            case PipelineDescriptor ignored -> "pipeline";
            case ProcessorDescriptor ignored -> "processor";
            default -> throw new SigletError("Unknown item type");
        };
    }




    default Map<String, BaseDescriptor> getUniqueNamedConfigItems(YamlDescriptor config) {

        Map<String, BaseDescriptor> namedConfigItems = new HashMap<>();

        namedConfigItems.putAll(config.getReceivers().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity())));

        namedConfigItems.putAll(config.getExporters().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity())));

        namedConfigItems.putAll(config.getPipelines().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity())));

        namedConfigItems.putAll(config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .collect(Collectors.toMap(c-> c.getName().getValue(), Function.identity())));

        return namedConfigItems;
    }

}

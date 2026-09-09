package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InternalMetricsExporterValidator implements YamlDescriptorValidator {

    @Override
    public void validate(YamlDescriptor yamlDescriptor) {

        Map<String, BaseDescriptor> exporters = yamlDescriptor.getExporters().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity()));

        if (yamlDescriptor.getGlobalConfig().getInternalMetricsGrpcExporter() != null) {
            BaseDescriptor descriptor = exporters.get(yamlDescriptor.getGlobalConfig().getInternalMetricsGrpcExporter().getValue());
            if (descriptor instanceof ExporterDescriptor exporterDescriptor) {
                if (!exporterDescriptor.getType().getValue().equals("grpc")) {
                    throw new SigletError(String.format("The internal-metrics-grpc-exporter [%s] at %s must be a grpc exporter",
                            exporterDescriptor.getName().getValue(),
                            exporterDescriptor.getLocation().print()));
                }
            } else {
                throw new SigletError(String.format("The internal-metrics-grpc-exporter [%s] is not defined in the exporters section",
                        yamlDescriptor.getGlobalConfig().getInternalMetricsGrpcExporter().getValue()));
            }
        }
    }
}

package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.ExporterDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.List;

public class InternalMetricsExporterValidator implements YamlDescriptorValidator {


    @Override
    public void validate(YamlDescriptor config) {

        List<String> exporters = config.getExporters().stream().map(ExporterDescriptor::getName).map(StringValue::getValue).toList();

        String internalMetricsExporter = config.getGlobalConfig().getInternalMetricsExporter() != null ? config.getGlobalConfig().getInternalMetricsExporter().getValue() : null;
        if (internalMetricsExporter != null && !exporters.contains(internalMetricsExporter)) {

            throw new SigletError(String.format("Cannot find exporter [%s] for internal metrics exporter",
                    internalMetricsExporter));
        }


    }

}

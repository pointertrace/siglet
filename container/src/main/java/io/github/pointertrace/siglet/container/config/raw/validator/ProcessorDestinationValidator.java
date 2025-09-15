package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.List;
import java.util.Map;

public class ProcessorDestinationValidator implements RawConfigValidator {


    @Override
    public void validate(RawConfig config) {
        Map<String, BaseConfig> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .forEach(processor -> validateProcessorDestination(processor, namedConfigItems));

    }

    private void validateProcessorDestination(ProcessorConfig processor, Map<String, BaseConfig> namedConfigItems) {
        List<LocatedString> destinations = processor.getTo();
        for (LocatedString destination : destinations) {
            String destinationValue = destination.getValue().contains(":") ?
                    destination.getValue().split(":")[1] : destination.getValue();
            BaseConfig destinationConfig = namedConfigItems.get(destinationValue);
            if (destinationConfig == null) {
                throw new SigletError(String.format("Processor [%s] at %s has [%s] as destination and there " +
                                                    "is " +
                                                    "no processor, exporter or pipeline with that name.",
                        processor.getName(),
                        processor.getLocation(), destinationValue));
            } else if (!(destinationConfig instanceof ProcessorConfig) && !(destinationConfig instanceof ExporterConfig)
                       && !(destinationConfig instanceof PipelineConfig)) {
                throw new SigletError(String.format("Processor [%s] at %s has %s [%s] as destination and it " +
                                                    "should be a " +
                                                    "processor, exporter or pipeline.", processor.getName(),
                        processor.getLocation(), getItemType(destinationConfig), destinationConfig.getName()));
            }


        }
    }


}

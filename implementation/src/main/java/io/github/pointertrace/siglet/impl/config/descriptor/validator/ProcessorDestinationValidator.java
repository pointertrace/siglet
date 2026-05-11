package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.*;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.List;
import java.util.Map;

public class ProcessorDestinationValidator implements YamlDescriptorValidator {


    @Override
    public void validate(YamlDescriptor config) {
        Map<String, BaseDescriptor> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().stream()
                .flatMap(pipelineConfig -> pipelineConfig.getProcessors().stream())
                .forEach(processor -> validateProcessorDestination(processor, namedConfigItems));

    }

    private void validateProcessorDestination(ProcessorDescriptor processorDescriptor, Map<String, BaseDescriptor> namedConfigItems) {
        List<StringValue> destinations = processorDescriptor.getTo();
        for (StringValue destination : destinations) {
            String destinationValue = destination.getValue().contains(":") ?
                    destination.getValue().split(":")[1] : destination.getValue();
            BaseDescriptor destinationConfig = namedConfigItems.get(destinationValue);
            if (destinationConfig == null) {
                throw new SigletError(String.format("Processor [%s] at %s has [%s] as destination and there " +
                                "is no processor, exporter or pipeline with that name.",
                        processorDescriptor.getName().getValue(),
                        processorDescriptor.getLocation().print(),
                        destinationValue));
            } else if (!(destinationConfig instanceof ProcessorDescriptor) && !(destinationConfig instanceof ExporterDescriptor)
                    && !(destinationConfig instanceof PipelineDescriptor)) {
                throw new SigletError(String.format("Processor [%s] at %s has %s [%s] as destination and it " +
                                "should be a processor, exporter or pipeline.",
                        processorDescriptor.getName().getValue(),
                        processorDescriptor.getLocation().print(),
                        getItemType(destinationConfig), destinationConfig.getName().getValue()));
            }


        }
    }


}

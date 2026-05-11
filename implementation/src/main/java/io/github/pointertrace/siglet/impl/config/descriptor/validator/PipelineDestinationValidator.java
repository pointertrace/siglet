package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.*;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.List;
import java.util.Map;

public class PipelineDestinationValidator implements YamlDescriptorValidator {

    @Override
    public void validate(YamlDescriptor config) {

        Map<String, BaseDescriptor> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().forEach(pipeline -> validatePipelineOrigin(pipeline, namedConfigItems));
    }

    private void validatePipelineOrigin(PipelineDescriptor pipelineDescriptor, Map<String, BaseDescriptor> namedConfigItems) {
        List<StringValue> destinations = pipelineDescriptor.getStart();
        for (StringValue destination : destinations) {
            BaseDescriptor destinationConfig = namedConfigItems.get(destination.getValue());
            if (destinationConfig == null) {
                throw new SigletError(String.format("Pipeline [%s] at %s has [%s] as destination and there is " +
                                                    "no processor or exporter with that name.",
                        pipelineDescriptor.getName().getValue(), pipelineDescriptor.getLocation().print(),
                        destination.getValue()));
            } else if (!(destinationConfig instanceof ProcessorDescriptor) && !(destinationConfig instanceof ExporterDescriptor)) {
                throw new SigletError(String.format("Pipeline [%s] at %s has %s [%s] as destination and it should be a " +
                                                    "processor or an exporter.", pipelineDescriptor.getName().getValue(),
                        pipelineDescriptor.getLocation().print(),
                        getItemType(destinationConfig), destinationConfig.getName().getValue()));
            }
        }
    }


}

package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.PipelineDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.ReceiverDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.parser.StringValue;

import java.util.Map;

public class PipelineOriginValidator implements YamlDescriptorValidator {


    @Override
    public void validate(YamlDescriptor config) {
        Map<String, BaseDescriptor> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().forEach(pipeline -> validatePipelineOrigin(pipeline, namedConfigItems));

    }

    private void validatePipelineOrigin(PipelineDescriptor pipelineDescriptor, Map<String, BaseDescriptor> namedConfigItems) {
        StringValue origin = pipelineDescriptor.getFrom();
        if (origin != null) {
            BaseDescriptor originConfig = namedConfigItems.get(origin.getValue());
            if (originConfig == null) {
                throw new SigletError(String.format("Pipeline [%s] at %s has [%s] as origin and there is no receiver with" +
                                " that name.", pipelineDescriptor.getName().getValue(),
                        pipelineDescriptor.getLocation().print(), origin.getValue()));
            } else if (!(originConfig instanceof ReceiverDescriptor)) {
                throw new SigletError(String.format("Pipeline [%s] at %s has %s [%s] as origin and it should be a " +
                                "receiver.", pipelineDescriptor.getName().getValue(),
                        pipelineDescriptor.getLocation().print(), getItemType(originConfig), origin.getValue()));
            }
        }
    }


}

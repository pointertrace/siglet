package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;

import java.util.List;

public class ComposedValidator implements YamlDescriptorValidator {

    private final List<YamlDescriptorValidator> validators = List.of(
            new UniqueNameValidator(),
            new ExporterOrphanValidator(),
            new PipelineDestinationValidator(),
            new PipelineOriginValidator(),
            new ProcessorDestinationValidator(),
            new ReceiverOrphanValidator()
    );

    public ComposedValidator() {
    }

    @Override
    public void validate(YamlDescriptor config) {
        validators.forEach(validator -> validator.validate(config));
    }


}

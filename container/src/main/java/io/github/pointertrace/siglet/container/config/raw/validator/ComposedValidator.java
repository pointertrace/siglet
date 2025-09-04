package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.config.raw.RawConfig;

import java.util.List;

public class ComposedValidator implements RawConfigValidator {

    private final List<RawConfigValidator> validators = List.of(
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
    public void validate(RawConfig config) {
        validators.forEach(validator -> validator.validate(config));
    }
}

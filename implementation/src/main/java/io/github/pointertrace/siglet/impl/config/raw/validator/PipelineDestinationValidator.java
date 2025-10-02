package io.github.pointertrace.siglet.impl.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.raw.*;

import java.util.List;
import java.util.Map;

public class PipelineDestinationValidator implements RawConfigValidator {

    @Override
    public void validate(RawConfig config) {

        Map<String, BaseConfig> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().forEach(pipeline -> validatePipelineOrigin(pipeline, namedConfigItems));
    }

    private void validatePipelineOrigin(PipelineConfig pipeline, Map<String, BaseConfig> namedConfigItems) {
        List<LocatedString> destinations = pipeline.getStart();
        for (LocatedString destination : destinations) {
            BaseConfig destinationConfig = namedConfigItems.get(destination.getValue());
            if (destinationConfig == null) {
                throw new SigletError(String.format("Pipeline [%s] at %s has [%s] as destination and there is " +
                                                    "no processor or exporter with that name.", pipeline.getName(),
                        pipeline.getLocation(), destination.getValue()));
            } else if (!(destinationConfig instanceof ProcessorConfig) && !(destinationConfig instanceof ExporterConfig)) {
                throw new SigletError(String.format("Pipeline [%s] at %s has %s [%s] as destination and it should be a " +
                                                    "processor or an exporter.", pipeline.getName(),
                        pipeline.getLocation(),
                        getItemType(destinationConfig), destinationConfig.getName()));
            }
        }
    }


}

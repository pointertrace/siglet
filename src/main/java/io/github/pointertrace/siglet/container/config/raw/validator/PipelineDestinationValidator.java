package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.raw.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineDestinationValidator implements RawConfigValidator {

    @Override
    public void Validate(RawConfig config) {

        Map<String, BaseConfig> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().forEach(pipeline -> validatePipelineOrigin(pipeline, namedConfigItems));
    }

    private void validatePipelineOrigin(PipelineConfig pipeline, Map<String, BaseConfig> namedConfigItems) {
        List<LocatedString> destinations = pipeline.getStart();
        for (LocatedString destination : destinations) {
            BaseConfig destinationConfig = namedConfigItems.get(destination.getValue());
            if (destinationConfig == null) {
                throw new SigletError(String.format("Pipeline [%s] at %s has [%s] as destination and there is " +
                                                    "no processor with that name.", pipeline.getName(),
                        pipeline.getLocation(), destination.getValue()));
            } else if (!(destinationConfig instanceof ProcessorConfig)) {
                throw new SigletError(String.format("Pipeline [%s] at %s has %s [%s] as destination and it should be a " +
                                                    "processor.", pipeline.getName(), pipeline.getLocation(),
                        getItemType(destinationConfig), destinationConfig.getName()));
            }
        }
    }


}

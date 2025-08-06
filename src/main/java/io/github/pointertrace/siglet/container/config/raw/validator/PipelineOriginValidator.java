package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.raw.BaseConfig;
import io.github.pointertrace.siglet.container.config.raw.PipelineConfig;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.raw.ReceiverConfig;

import java.util.HashMap;
import java.util.Map;

public class PipelineOriginValidator implements RawConfigValidator {


    @Override
    public void Validate(RawConfig config) {
        Map<String, BaseConfig> namedConfigItems = getUniqueNamedConfigItems(config);

        config.getPipelines().forEach(pipeline -> validatePipelineOrigin(pipeline, namedConfigItems));

    }

    private void validatePipelineOrigin(PipelineConfig pipeline, Map<String, BaseConfig> namedConfigItems) {
        String origin = pipeline.getFrom();
        if (origin != null) {
            BaseConfig originConfig = namedConfigItems.get(origin);
            if (originConfig == null) {
                throw new SigletError(String.format("Pipeline [%s] at %s has [%s] as origin and there is no receiver with" +
                                                    " that name.", pipeline.getName(), pipeline.getLocation(), origin));
            } else if (!(originConfig instanceof ReceiverConfig)) {
                throw new SigletError(String.format("Pipeline [%s] at %s has %s [%s] as origin and it should be a " +
                                                    "receiver.", pipeline.getName(), pipeline.getLocation(),
                        getItemType(originConfig), origin));
            }
        }
    }


}

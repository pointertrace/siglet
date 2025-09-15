package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.raw.BaseConfig;
import io.github.pointertrace.siglet.container.config.raw.PipelineConfig;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReceiverOrphanValidator implements RawConfigValidator {

    @Override
    public void validate(RawConfig config) {
        Map<String, BaseConfig> receivers = config.getReceivers().stream()
                .collect(Collectors.toMap(BaseConfig::getName, Function.identity()));

        receivers.keySet()
                .removeAll(config.getPipelines().stream()
                        .map(PipelineConfig::getFrom)
                        .filter(Objects::nonNull)
                        .toList());


        if (!receivers.isEmpty()) {
            throw new SigletError(receivers.values().stream()
                    .map(receiver -> String.format("%s[%s] at %s", Describable.prefix(2),
                            receiver.getName(), receiver.getLocation()))
                    .collect(Collectors.joining("\n", "The following receivers are orphaned:\n", "")));
        }
    }
}

package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.BaseDescriptor;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReceiverOrphanValidator implements YamlDescriptorValidator {

    @Override
    public void validate(YamlDescriptor config) {
        Map<String, BaseDescriptor> receivers = config.getReceivers().stream()
                .collect(Collectors.toMap(c -> c.getName().getValue(), Function.identity()));

        receivers.keySet()
                .removeAll(config.getPipelines().stream()
                        .map(p -> p.getFrom().getValue())
                        .filter(Objects::nonNull)
                        .toList());


        if (!receivers.isEmpty()) {
            throw new SigletError(receivers.values().stream()
                    .map(receiver -> String.format("    [%s] at %s",
                            receiver.getName().getValue(),
                            receiver.getLocation().print()))
                    .collect(Collectors.joining("\n", "The following receivers are orphaned:\n", "")));
        }
    }
}

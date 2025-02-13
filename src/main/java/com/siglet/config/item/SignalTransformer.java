package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.parser.node.ValueTransformer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignalTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) {
        if (value == null) {
            throw new SigletError("The value is null");
        }

        if (!(value instanceof String strValue)) {
            throw new SigletError("The value is of type " + value.getClass().getName() + " and it should be a string!");
        }
        try {
            return Signal.valueOf(strValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SigletError(String.format("The value [%s] is not a valid signal type [%s]", value,
                    Stream.of(Signal.values())
                            .map(Enum::name)
                            .map(v -> v.replace('_', '-').toLowerCase())
                            .collect(Collectors.joining(", "))));
        }
    }
}

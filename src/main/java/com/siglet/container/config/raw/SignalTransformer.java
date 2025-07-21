package com.siglet.container.config.raw;

import com.siglet.parser.ValueTransformer;
import com.siglet.parser.ValueTransformerException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignalTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) throws ValueTransformerException {
        if (value == null) {
            throw new ValueTransformerException("The value is null");
        }

        if (!(value instanceof String strValue)) {
            throw new ValueTransformerException("The value is of type " + value.getClass().getName() +
                    " and it should be a string!");
        }
        try {
            SignalType signalType = SignalType.valueOf(strValue.toUpperCase());
            if (signalType.isInternal()) {
                throw new ValueTransformerException(String.format("SignalType value %s cannot be used in configuration!"
                        , signalType.name()));

            }
            return SignalType.valueOf(strValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValueTransformerException(String.format("The value [%s] is not a valid signal type [%s]!", value,
                    Stream.of(SignalType.values())
                            .filter(st -> !st.isInternal())
                            .map(Enum::name)
                            .map(v -> v.replace('_', '-').toLowerCase())
                            .collect(Collectors.joining(", "))));
        }
    }
}

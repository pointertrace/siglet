package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.ValueTransformerException;

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
            return OtelSignalType.valueOf(strValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValueTransformerException(String.format("The value [%s] is not a valid signal type [%s]!", value,
                    getOteltypesDescription()));
        }

    }

    private String getOteltypesDescription() {
        return Stream.of(OtelSignalType.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}

package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siget.parser.ValueTransformer;
import io.github.pointertrace.siget.parser.ValueTransformerException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessorKindTransformer implements ValueTransformer {

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
            strValue = strValue.replace("-", "_").toUpperCase();
            return ProcessorKind.valueOf(strValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValueTransformerException(String.format("The value [%s] is not a valid sigletClass kind [%s]", value,
                    Stream.of(ProcessorKind.values())
                            .map(Enum::name)
                            .map(v -> v.replace('_', '-').toLowerCase())
                            .collect(Collectors.joining(", "))));
        }
    }
}

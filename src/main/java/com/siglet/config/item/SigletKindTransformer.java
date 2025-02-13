package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.parser.node.ValueTransformer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SigletKindTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) {
        if (value == null) {
            throw new SigletError("The value is null");
        }

        if (!(value instanceof String strValue)) {
            throw new SigletError("The value is of type " + value.getClass().getName() + " and it should be a string!");
        }
        try {
            strValue = strValue.replace("-", "_").toUpperCase();
            return SigletKind.valueOf(strValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SigletError(String.format("The value [%s] is not a valid siglet kind [%s]", value,
                    Stream.of(SigletKind.values())
                            .map(Enum::name)
                            .map(v -> v.replace('_', '-').toLowerCase())
                            .collect(Collectors.joining(", "))));
        }
    }
}

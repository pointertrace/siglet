package io.github.pointertrace.siglet.impl.config.raw;

import io.github.pointertrace.siglet.parser.ValueTransformer;
import io.github.pointertrace.siglet.parser.ValueTransformerException;

public class LocatedStringTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) throws ValueTransformerException {
        if (value == null) {
            throw new ValueTransformerException("The value is null");
        }
        if (! (value instanceof String strValue)) {
            throw new ValueTransformerException("The value is of type " + value.getClass().getName() +
                    " and it should be a string!");
        }
        LocatedString locatedString = new LocatedString();
        locatedString.setValue(strValue);
        return locatedString;
    }
}

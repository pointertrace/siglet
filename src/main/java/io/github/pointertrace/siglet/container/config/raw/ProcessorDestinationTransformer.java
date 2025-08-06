package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siget.parser.ValueTransformer;
import io.github.pointertrace.siget.parser.ValueTransformerException;

public class ProcessorDestinationTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) throws ValueTransformerException {
        if (value == null) {
            throw new ValueTransformerException("The value is null!");
        }
        if (! (value instanceof String strValue)) {
            throw new ValueTransformerException("The value is of type " + value.getClass().getName() +
                    " and it should be a string!");
        }

        if (strValue.split(":").length > 2) {
            throw new ValueTransformerException("The value of a destination must be <destination> or " +
                                                "<alias>:<destination>!");

        }
        LocatedString locatedString = new LocatedString();
        locatedString.setValue(strValue);
        return locatedString;
    }
}

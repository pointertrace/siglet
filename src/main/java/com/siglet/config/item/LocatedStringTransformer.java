package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.parser.node.ValueTransformer;

public class LocatedStringTransformer implements ValueTransformer {

    @Override
    public Object transform(Object value) {
        if (value == null) {
            throw new SigletError("The value is null");
        }
        if (! (value instanceof String strValue)) {
            throw new SigletError("The value is of type " + value.getClass().getName() + " and it should be a string!");
        }
        LocatedString locatedString = new LocatedString();
        locatedString.setValue(strValue);
        return locatedString;
    }
}

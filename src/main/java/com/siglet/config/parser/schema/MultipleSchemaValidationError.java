package com.siglet.config.parser.schema;

import com.siglet.config.located.Location;

import java.util.Collections;
import java.util.List;

public class MultipleSchemaValidationError extends SchemaValidationError {

    private final List<? extends SchemaValidationError> validationExceptions;

    public MultipleSchemaValidationError(Location location, String message, List<? extends SchemaValidationError> validationExceptions) {
        super(location, message);
        this.validationExceptions = validationExceptions;
    }


    public List<? extends SchemaValidationError> getValidationExceptions() {
        return Collections.unmodifiableList(validationExceptions);
    }

    @Override
    protected String explain(int level) {
        StringBuilder msg = new StringBuilder(String.format("%s%s %s because:", PREFIX.repeat(level),
                getLocation().describe(), getMessage()));
        for (SchemaValidationError causeExc : getValidationExceptions()) {
            msg.append('\n');
            msg.append(causeExc.explain(level + 1));
        }
        return msg.toString();
    }
}

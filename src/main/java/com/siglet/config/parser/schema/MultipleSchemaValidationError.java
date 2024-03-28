package com.siglet.config.parser.schema;

import java.util.Collections;
import java.util.List;

public class MultipleSchemaValidationError extends SchemaValidationException {

    private final List<SingleSchemaValidationException> validationExceptions;

    public MultipleSchemaValidationError(String message, List<SingleSchemaValidationException> validationExceptions) {
        super(message);
        this.validationExceptions = validationExceptions;
    }


    public List<SingleSchemaValidationException> getValidationExceptions() {
        return Collections.unmodifiableList(validationExceptions);
    }
}

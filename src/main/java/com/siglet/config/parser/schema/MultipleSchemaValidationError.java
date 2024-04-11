package com.siglet.config.parser.schema;

import java.util.Collections;
import java.util.List;

public class MultipleSchemaValidationError extends SchemaValidationError {

    private final List<SingleSchemaValidationError> validationExceptions;

    public MultipleSchemaValidationError(String message, List<SingleSchemaValidationError> validationExceptions) {
        super(message);
        this.validationExceptions = validationExceptions;
    }


    public List<SingleSchemaValidationError> getValidationExceptions() {
        return Collections.unmodifiableList(validationExceptions);
    }
}

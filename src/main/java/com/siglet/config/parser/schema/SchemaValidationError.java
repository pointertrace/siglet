package com.siglet.config.parser.schema;

import com.siglet.SigletError;

public abstract class SchemaValidationError extends SigletError {

    protected SchemaValidationError(String message) {
        super(message);
    }

}

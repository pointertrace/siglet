package com.siglet.config.parser.schema;


import com.siglet.config.located.Location;

import java.io.Serial;

public class SingleSchemaValidationError extends SchemaValidationError {

    @Serial
    private static final long serialVersionUID = -30768105953872818L;

    public SingleSchemaValidationError(Location location, String message) {
        super(location, message);
    }

    public SingleSchemaValidationError(Location location, String message, SchemaValidationError cause) {
        super(location, message, cause);
    }

    @Override
    protected String explain(int level) {
        if (getCause() != null) {
            return String.format("%s(%d:%d) %s because:%n%s", PREFIX.repeat(level), getLocation().getLine(),
                    getLocation().getColumn(), getMessage(), getCause().explain(level + 1));
        } else {
            return String.format("%s(%d:%d) %s", PREFIX.repeat(level), getLocation().getLine(),
                    getLocation().getColumn(), getMessage());
        }
    }

}

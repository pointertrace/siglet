package com.siglet.config.parser.schema;


import com.siglet.config.located.Location;

import java.io.Serial;

public class SingleSchemaValidationError extends SchemaValidationError {

    @Serial
    private static final long serialVersionUID = -30768105953872818L;

    public SingleSchemaValidationError(Location location, String localMessage) {
        super(location, localMessage);
    }

    public SingleSchemaValidationError(Location location, String message, SchemaValidationError cause) {
        super(location, message, cause);
    }

    @Override
    protected String explain(int level) {
        if (getCause() != null) {
            return String.format("%s%s %s because:%n%s", PREFIX.repeat(level), getLocation().describe(), getLocalMessage(),
                    getCause().explain(level + 1));
        } else {
            return String.format("%s%s %s", PREFIX.repeat(level), getLocation().describe(), getLocalMessage());
        }
    }

}

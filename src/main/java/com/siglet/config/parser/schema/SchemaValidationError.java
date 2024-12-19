package com.siglet.config.parser.schema;

import com.siglet.SigletError;
import com.siglet.config.located.Location;

public abstract class SchemaValidationError extends SigletError {

    private static final long serialVersionUID = -30768105953872818L;

    protected static final String PREFIX = "  ";

    private final Location location;

    protected SchemaValidationError(Location location, String message) {
        super(message);
        this.location = location;
    }

    protected SchemaValidationError(Location location, String message, SchemaValidationError cause) {
        super(message, cause);
        this.location = location;
    }

    public SchemaValidationError getCause() {
        return (SchemaValidationError) super.getCause();
    }

    public Location getLocation() {
        return location;
    }

    public String explain() {
        return explain(0);

    }

    protected abstract String explain(int level);

}

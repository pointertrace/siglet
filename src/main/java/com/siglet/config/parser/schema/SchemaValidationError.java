package com.siglet.config.parser.schema;

import com.siglet.SigletError;
import com.siglet.config.located.Location;

public abstract class SchemaValidationError extends SigletError {

    private static final long serialVersionUID = -30768105953872818L;

    protected static final String PREFIX = "  ";

    private final Location location;

    private final String localMessage;

    protected SchemaValidationError(Location location, String localMessage) {
        super("");
        this.location = location;
        this.localMessage = localMessage;
    }

    protected SchemaValidationError(Location location, String localMessage, SchemaValidationError cause) {
        super("", cause);
        this.location = location;
        this.localMessage = localMessage;
    }

    @Override
    public SchemaValidationError getCause() {
        return (SchemaValidationError) super.getCause();
    }

    public Location getLocation() {
        return location;
    }

    public String getLocalMessage() {
        return localMessage;
    }

    public String getMessage() {
        return explain(0);

    }

    protected abstract String explain(int level);

}

package io.github.pointertrace.siglet.parser;

import io.github.pointertrace.siglet.parser.located.Location;

public abstract class SchemaValidationError extends RuntimeException {

    public static final String PREFIX = "  ";

    private final Location location;

    private final String localMessage;

    public SchemaValidationError(Location location, String localMessage) {
        super("");
        this.location = location;
        this.localMessage = localMessage;
    }

    public SchemaValidationError(Location location, String localMessage, SchemaValidationError cause) {
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

    public abstract String explain(int level);

}

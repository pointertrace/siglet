package com.siglet.config.parser.schema;


import com.siglet.config.parser.locatednode.Location;

public class SingleSchemaValidationException extends SchemaValidationException {

    private final Location location;
    public SingleSchemaValidationException(String message, Location location) {
        super(message);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("(%d:%d) %s",location.getLine(), location.getColumn(), getMessage());
    }
}

package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.located.Location;

public class SigletParserError extends RuntimeException {

    private final Location location;

    public SigletParserError(String message, Location location) {
        super(message);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}

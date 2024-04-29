package com.siglet.config.parser.node;

import com.siglet.SigletError;
import com.siglet.config.parser.locatednode.Location;

public class SigletParserError extends SigletError {

    private final Location location;

    public SigletParserError(String message, Location location) {
        super(message);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}

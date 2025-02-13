package com.siglet.config.located;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Located {

    Location getLocation();

    void setLocation(Location location);

    static void set(Object value, Location location) {
        if (value instanceof Located locatedValue) {
            locatedValue.setLocation(location);
        }
    }
}


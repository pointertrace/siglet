package com.siglet.config.located;

public interface Located {

    Location getLocation();

    void setLocation(Location location);

    static void set(Object value, Location location) {
        if (value instanceof Located locatedValue) {
            locatedValue.setLocation(location);
        }
    }
}


package io.github.pointertrace.siglet.parser.located;

public interface Located {

    Location getLocation();

    void setLocation(Location location);

    static void set(Object value, Location location) {
        if (value instanceof Located) {
            Located locatedValue = (Located) value;
            locatedValue.setLocation(location);
        }
    }
}

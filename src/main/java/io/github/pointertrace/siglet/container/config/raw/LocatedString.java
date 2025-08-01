package io.github.pointertrace.siglet.container.config.raw;


import io.github.pointertrace.siget.parser.located.Located;
import io.github.pointertrace.siget.parser.located.Location;

public class LocatedString implements Located {

    private String value;

    private Location location;

    public LocatedString(){
    }

    public LocatedString(String value, Location location) {
        this.value = value;
        this.location = location;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

}

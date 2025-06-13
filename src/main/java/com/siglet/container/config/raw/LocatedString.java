package com.siglet.container.config.raw;


import com.siglet.api.parser.located.Located;
import com.siglet.api.parser.located.Location;

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

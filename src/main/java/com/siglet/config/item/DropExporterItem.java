package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.located.Location;

public class DropExporterItem extends ExporterItem {

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public void setName(String name) {
        throw new SigletError("Drop exporter has a already defined name!");
    }

    @Override
    public void setLocation(Location location) {
        throw new SigletError("Drop exporter has a already defined location!");
    }

    @Override
    public Location getLocation() {
        return Location.of(0, 0);
    }

    @Override
    public String getUri() {
        return "drop:teste";
    }

    @Override
    public String describe(int level) {
        throw new SigletError("Drop exporter should not be described!");
    }

}

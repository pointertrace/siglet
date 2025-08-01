package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siget.parser.located.Location;

public class DropExporterConfig extends ExporterConfig {

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
    public String describe(int level) {
        throw new SigletError("Drop exporter should not be described!");
    }

}

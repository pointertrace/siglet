package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.located.Location;

import java.net.InetSocketAddress;

// TODO rever herança
public class DropExporterItem extends ExporterItem {

    public ValueItem<String> getName() {
        return new ValueItem<String>(Location.of(0,0),"drop");
    }

    public void setName(ValueItem<String> name) {
        throw new SigletError("Drop exporter has a already defined name!");
    }
    public void setLocation(Location location) {
        throw new SigletError("Drop exporter has a already defined location!");
    }

    public Location getLocation() {
        return Location.of(0,0);
    }
    @Override
    public String getUri() {
        return "drop:teste";
    }
}

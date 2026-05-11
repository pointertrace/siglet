package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.Locatable;
import io.github.pointertrace.siglet.parser.Location;

import java.net.InetSocketAddress;

public class LocatedInetSocketAddress implements Locatable {

    private InetSocketAddress inetSocketAddress;

    private Location location;

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}

package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.Location;

import java.net.InetSocketAddress;

public class LocatedInetSocketAddress {

    private final InetSocketAddress inetSocketAddress;

    private final Location location;

    public LocatedInetSocketAddress(InetSocketAddress inetSocketAddress, Location location) {
        this.inetSocketAddress = inetSocketAddress;
        this.location = location;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }


    public Location getLocation() {
        return location;
    }


}

package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.Location;

import java.net.InetSocketAddress;
import java.net.URL;

public class LocatedUrl {

    private final URL url;

    private final Location location;

    public LocatedUrl(URL url, Location location) {
        this.url = url;
        this.location = location;
    }

    public URL getUrl() {
        return url;
    }

    public Location getLocation() {
        return location;
    }


}

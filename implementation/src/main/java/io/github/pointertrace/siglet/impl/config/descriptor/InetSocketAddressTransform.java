package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.Location;
import io.github.pointertrace.siglet.parser.ValueTransform;

import java.util.Objects;

public class InetSocketAddressTransform implements ValueTransform<String, LocatedInetSocketAddress> {


    @Override
    public LocatedInetSocketAddress transform(String value, Location location) {


        String[] parts = value.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException(String.format("(%s) Address must be a valid IP:port format",location.print()));
        }

        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        return new LocatedInetSocketAddress(new java.net.InetSocketAddress(host, port), location);
    }
}

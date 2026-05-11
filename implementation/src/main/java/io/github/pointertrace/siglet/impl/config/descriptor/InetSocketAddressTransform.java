package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.parser.ValueTransform;

import java.util.Objects;

public class InetSocketAddressTransform implements ValueTransform<String, LocatedInetSocketAddress> {

    @Override
    public LocatedInetSocketAddress transform(String value) {
        Objects.requireNonNull(value, "value cannot be null");

        String[] parts = value.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid InetSocketAddress format: " + value);
        }

        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        LocatedInetSocketAddress locatedInetSocketAddress = new LocatedInetSocketAddress();

        locatedInetSocketAddress.setInetSocketAddress(new java.net.InetSocketAddress(host, port));

        return locatedInetSocketAddress;

    }
}

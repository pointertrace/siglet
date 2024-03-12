package com.siglet.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class GrpcReceiver extends Receiver {

    private final InetSocketAddress address;

    public GrpcReceiver(String name, InetSocketAddress address) {
        super(name);
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}

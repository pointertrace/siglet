package com.siglet.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class GrpcReceiver extends Receiver {

    private final String address;

    public GrpcReceiver(String name, String address) {
        super(name, null);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}

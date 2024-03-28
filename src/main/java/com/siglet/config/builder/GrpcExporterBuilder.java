package com.siglet.config.builder;

import java.net.InetSocketAddress;

public class GrpcExporterBuilder extends ExporterBuilder{


    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }


}

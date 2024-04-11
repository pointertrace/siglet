package com.siglet.config.item;

import java.net.InetSocketAddress;

public class GrpcExporterItem extends ExporterItem {


    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }


    @Override
    public String getUri() {
        return "otelgrpc:" + address.getHostName() + ":" + address.getPort();
    }
}

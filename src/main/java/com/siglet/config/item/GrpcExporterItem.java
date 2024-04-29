package com.siglet.config.item;

import java.net.InetSocketAddress;

public class GrpcExporterItem extends ExporterItem {


    private ValueItem<InetSocketAddress> address;

    public ValueItem<InetSocketAddress> getAddress() {
        return address;
    }

    public void setAddress(ValueItem<InetSocketAddress> address) {
        this.address = address;
    }


    @Override
    public String getUri() {
        return "otelgrpc:" + address.getValue().getHostName() + ":" + address.getValue().getPort();
    }
}

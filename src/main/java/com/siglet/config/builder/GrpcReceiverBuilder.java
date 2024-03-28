package com.siglet.config.builder;

import java.net.InetSocketAddress;

public class GrpcReceiverBuilder extends ReceiverBuilder {


    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }


}

package com.siglet.config.item;

import java.net.InetSocketAddress;

public class GrpcReceiverItem extends ReceiverItem {


    private ValueItem<InetSocketAddress> address;

    private ValueItem<String> signalType;

    public ValueItem<InetSocketAddress> getAddress() {
        return address;
    }

    public void setAddress(ValueItem<InetSocketAddress> address) {
        this.address = address;
    }

    public ValueItem<String> getSignalType() {
        return signalType;
    }

    public void setSignalType(ValueItem<String> signalType) {
        this.signalType = signalType;
    }

    @Override
    public String getUri() {
        return String.format("otelgrpc:%s:%d?signalType=%s", address.getValue().getHostName(),
                address.getValue().getPort(), signalType.getValue());
    }
}

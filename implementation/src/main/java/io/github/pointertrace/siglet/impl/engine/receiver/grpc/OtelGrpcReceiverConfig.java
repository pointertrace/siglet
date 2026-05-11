package io.github.pointertrace.siglet.impl.engine.receiver.grpc;

import io.github.pointertrace.siglet.impl.config.descriptor.LocatedInetSocketAddress;

public class OtelGrpcReceiverConfig {

    private LocatedInetSocketAddress address;

    public LocatedInetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(LocatedInetSocketAddress address) {
        this.address = address;
    }
}

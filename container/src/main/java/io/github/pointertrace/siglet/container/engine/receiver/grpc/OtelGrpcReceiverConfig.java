package io.github.pointertrace.siglet.container.engine.receiver.grpc;

import io.github.pointertrace.siglet.parser.Describable;
import io.github.pointertrace.siglet.parser.located.Location;

import java.net.InetSocketAddress;

public class OtelGrpcReceiverConfig implements Describable {

    private InetSocketAddress address;

    private Location addressLocation;


    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Location getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Location addressLocation) {
        this.addressLocation = addressLocation;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(Describable.prefix(level));
        sb.append(getAddressLocation());
        sb.append("  address: ");
        sb.append(getAddress());

        return sb.toString();
    }

}

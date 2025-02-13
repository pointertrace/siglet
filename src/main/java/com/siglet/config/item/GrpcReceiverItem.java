package com.siglet.config.item;

import com.siglet.config.located.Location;

import java.net.InetSocketAddress;

public class GrpcReceiverItem extends ReceiverItem {

    private InetSocketAddress address;

    private Location addressLocation;

    private Signal signal;

    private Location signalTypeLocation;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Signal getSignal() {
        return signal;
    }

    public void setSignal(Signal signal) {
        this.signal= signal;
    }

    @Override
    public String getUri() {
        return String.format("otelgrpc:%s:%d?signalType=%s", address.getHostName(), address.getPort(),
                signal.name().toLowerCase());
    }

    public Location getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Location addressLocation) {
        this.addressLocation = addressLocation;
    }

    public Location getSignalTypeLocation() {
        return signalTypeLocation;
    }

    public void setSignalTypeLocation(Location signalTypeLocation) {
        this.signalTypeLocation = signalTypeLocation;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  GrpcReceiver");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(addressLocation.describe());
        sb.append("  address: ");
        sb.append(address);
        sb.append("\n");

        if (signal != null) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(signalTypeLocation.describe());
            sb.append("  signal: ");
            sb.append(signal.name().toLowerCase());
            sb.append("\n");
        }

        return sb.toString();
    }
}

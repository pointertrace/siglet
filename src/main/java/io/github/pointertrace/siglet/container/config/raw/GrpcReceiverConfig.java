package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siget.parser.located.Location;

import java.net.InetSocketAddress;

public class GrpcReceiverConfig extends ReceiverConfig {

    private InetSocketAddress address;

    private Location addressLocation;

    private OtelSignalType otelSignalType;

    private Location signalTypeLocation;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public OtelSignalType getSignal() {
        return otelSignalType;
    }

    public void setSignal(OtelSignalType otelSignalType) {
        this.otelSignalType = otelSignalType;
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
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  GrpcReceiverConfig");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(prefix(level + 1));
        sb.append(addressLocation.describe());
        sb.append("  address: ");
        sb.append(address);
        sb.append("\n");

        if (otelSignalType != null) {
            sb.append(prefix(level + 1));
            sb.append(signalTypeLocation.describe());
            sb.append("  signal: ");
            sb.append(otelSignalType.name().toLowerCase());
            sb.append("\n");
        }

        return sb.toString();
    }
}

package com.siglet.container.config.raw;

import com.siglet.api.parser.located.Location;

import java.net.InetSocketAddress;

public class GrpcExporterConfig extends ExporterConfig {

    private InetSocketAddress address;

    private Location addressLocation;

    private Integer batchSizeInSignals;

    private Location batchSizeInSignalsLocation;

    private Integer batchTimeoutInMillis;

    private Location batchTimeoutInMillisLocation;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Integer getBatchSizeInSignals() {
        return batchSizeInSignals;
    }

    public void setBatchSizeInSignals(Integer batchSizeInSignals) {
        this.batchSizeInSignals = batchSizeInSignals;
    }

    public Integer getBatchTimeoutInMillis() {
        return batchTimeoutInMillis;
    }

    public void setBatchTimeoutInMillis(Integer batchTimeoutInMillis) {
        this.batchTimeoutInMillis = batchTimeoutInMillis;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(prefix(level));
        sb.append(getLocation().describe());
        sb.append("  GrpcExporterConfig");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(prefix(level + 1));
        sb.append(addressLocation.describe());
        sb.append("  address: ");
        sb.append(address);
        sb.append("\n");

        if (batchSizeInSignals != null) {
            sb.append(prefix(level + 1));
            sb.append(batchSizeInSignalsLocation.describe());
            sb.append("  batch-size-in-signal: ");
            sb.append(batchSizeInSignals);
            sb.append("\n");
        }

        if (batchTimeoutInMillis != null) {
            sb.append(prefix(level + 1));
            sb.append(batchTimeoutInMillisLocation.describe());
            sb.append("  batch-timeout-in-millis: ");
            sb.append(batchTimeoutInMillis);
            sb.append("\n");
        }

        return sb.toString();
    }

    public Location getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Location addressLocation) {
        this.addressLocation = addressLocation;
    }

    public Location getBatchSizeInSignalsLocation() {
        return batchSizeInSignalsLocation;
    }

    public void setBatchSizeInSignalsLocation(Location batchSizeInSignalsLocation) {
        this.batchSizeInSignalsLocation = batchSizeInSignalsLocation;
    }

    public Location getBatchTimeoutInMillisLocation() {
        return batchTimeoutInMillisLocation;
    }

    public void setBatchTimeoutInMillisLocation(Location batchTimeoutInMillisLocation) {
        this.batchTimeoutInMillisLocation = batchTimeoutInMillisLocation;
    }
}

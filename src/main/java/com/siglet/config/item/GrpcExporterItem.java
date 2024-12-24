package com.siglet.config.item;

import java.net.InetSocketAddress;

public class GrpcExporterItem extends ExporterItem {

    private ValueItem<InetSocketAddress> address;

    private ValueItem<Integer> batchSizeInSignals;

    private ValueItem<Integer> batchTimeoutInMillis;

    private GrpcExporterUri grpcExporterUri;

    public ValueItem<InetSocketAddress> getAddress() {
        return address;
    }

    public void setAddress(ValueItem<InetSocketAddress> address) {
        this.address = address;
    }

    public ValueItem<Integer> getBatchSizeInSignals() {
        return batchSizeInSignals;
    }

    public void setBatchSizeInSignals(ValueItem<Integer> batchSizeInSignals) {
        this.batchSizeInSignals = batchSizeInSignals;
    }

    public ValueItem<Integer> getBatchTimeoutInMillis() {
        return batchTimeoutInMillis;
    }

    public void setBatchTimeoutInMillis(ValueItem<Integer> batchTimeoutInMillis) {
        this.batchTimeoutInMillis = batchTimeoutInMillis;
    }

    @Override
    public String getUri() {
        if (grpcExporterUri == null) {
            grpcExporterUri = GrpcExporterUri.of(address.getValue(),
                    batchSizeInSignals != null ? batchSizeInSignals.getValue() : null,
                    batchTimeoutInMillis != null ? batchTimeoutInMillis.getValue() : null);
        }
        return grpcExporterUri.toString();
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  GrpcExporterItem");
        sb.append("\n");
        sb.append(getName().getDescriptionPrefix(level + 1));
        sb.append(getName().getLocation().describe());
        sb.append("  name");
        sb.append("\n");
        sb.append(getName().describe(level + 2));
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(address.getLocation().describe());
        sb.append("  address");
        sb.append("\n");
        sb.append(address.describe(level + 2));
        sb.append("\n");

        if (batchSizeInSignals != null) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(batchSizeInSignals.getLocation().describe());
            sb.append("  batch-size-in-signal");
            sb.append("\n");
            sb.append(batchSizeInSignals.describe(level + 2));
            sb.append("\n");
        }

        if (batchTimeoutInMillis != null) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(batchTimeoutInMillis.getLocation().describe());
            sb.append("  batch-timeout-in-millis");
            sb.append("\n");
            sb.append(batchTimeoutInMillis.describe(level + 2));
            sb.append("\n");
        }

        return sb.toString();
    }
}

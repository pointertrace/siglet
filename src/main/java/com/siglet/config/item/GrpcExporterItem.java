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
}

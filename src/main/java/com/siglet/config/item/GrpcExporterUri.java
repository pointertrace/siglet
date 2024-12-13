package com.siglet.config.item;

import com.siglet.SigletError;

import java.net.InetSocketAddress;

public class GrpcExporterUri {

    private final InetSocketAddress address;

    private final Integer batchSizeInSignals;

    private final Integer batchTimeoutInMillis;

    protected GrpcExporterUri(InetSocketAddress address, Integer batchSizeInSignals, Integer batchTimeoutInMillis) {
        this.address = address;
        this.batchSizeInSignals = batchSizeInSignals;
        this.batchTimeoutInMillis = batchTimeoutInMillis;
    }

    public static GrpcExporterUri of(InetSocketAddress address, Integer batchSizeInSignals, Integer batchTimeoutInMillis) {
        return new GrpcExporterUri(address, batchSizeInSignals, batchTimeoutInMillis);
    }

    public static GrpcExporterUri of(String uri) {
        String[] schemeParams = uri.split("\\?");
        String scheme = schemeParams[0];
        String[] schemeParts = scheme.split(":");
        if (!schemeParts[0].equals("otelgrpc")) {
            throw new SigletError("Invalid scheme: " + scheme);
        }
        try {
            String host = schemeParts[1];
            int port = Integer.parseInt(schemeParts[2]);
            InetSocketAddress address = new InetSocketAddress(host, port);
            Integer batchSizeInSignals = null;
            Integer batchTimeout = null;
            if (schemeParams.length > 1) {
                String[] params = schemeParams[1].split("&");
                for (String param : params) {
                    String[] paramParts = param.split("=");
                    if (paramParts.length != 2) {
                        throw new SigletError("Invalid parameter: " + param);
                    }
                    switch (paramParts[0]) {
                        case "batchSizeInSignals":
                            batchSizeInSignals = Integer.parseInt(paramParts[1]);
                            break;
                        case "batchTimeoutInMillis":
                            batchTimeout = Integer.parseInt(paramParts[1]);
                            break;
                        default:
                            throw new SigletError("Invalid parameter: " + param);
                    }
                }
            }
            return new GrpcExporterUri(address, batchSizeInSignals, batchTimeout);
        } catch (NumberFormatException e) {
            throw new SigletError("Invalid numeric value " +  e.getMessage());
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Integer getBatchSizeInSignals() {
        return batchSizeInSignals;
    }

    public Integer getBatchTimeoutInMillis() {
        return batchTimeoutInMillis;
    }


    @Override
    public String toString() {
        StringBuilder uri = new StringBuilder()
                .append("otelgrpc:")
                .append(address.getHostName())
                .append(":")
                .append(address.getPort());

        boolean hasParam = false;
        if (batchSizeInSignals != null) {
            uri.append("?")
                    .append("batchSizeInSignals=")
                    .append(batchSizeInSignals);
            hasParam = true;
        }
        if (batchTimeoutInMillis != null) {
            if (hasParam) {
                uri.append("&");
            } else {
                uri.append("?");
            }
            uri.append("batchTimeoutInMillis=")
                    .append(batchTimeoutInMillis);
        }
        return uri.toString();
    }


}

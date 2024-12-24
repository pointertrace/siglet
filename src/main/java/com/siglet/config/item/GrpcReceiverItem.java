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

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  GrpcReceiverItem");
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

        if (signalType != null) {
            sb.append(getDescriptionPrefix(level + 1));
            sb.append(signalType.getLocation().describe());
            sb.append("  signal-type");
            sb.append("\n");
            sb.append(signalType.describe(level + 2));
        }

        return sb.toString();
    }
}

package com.siglet.config.item;

public class DebugReceiverItem extends ReceiverItem {

    private ValueItem<String> address;

    public ValueItem<String> getAddress() {
        return address;
    }

    public void setAddress(ValueItem<String> address) {
        this.address = address;
    }


    @Override
    public String getUri() {
        return address.getValue();
    }


    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder(getDescriptionPrefix(level));
        sb.append(getLocation().describe());
        sb.append("  DebugReceiverItem");
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
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

        return sb.toString();
    }
}

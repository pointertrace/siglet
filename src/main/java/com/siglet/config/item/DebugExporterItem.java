package com.siglet.config.item;

public class DebugExporterItem extends ExporterItem {


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
        sb.append("  DebugExporterItem");
        sb.append("\n");

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(address.getLocation().describe());
        sb.append("  address");
        sb.append("\n");
        sb.append(address.describe(level + 2));

        return sb.toString();
    }
}

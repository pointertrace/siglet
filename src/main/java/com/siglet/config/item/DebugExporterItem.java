package com.siglet.config.item;

import com.siglet.config.located.Location;

public class DebugExporterItem extends ExporterItem {

    private String address;

    private Location addressLocation;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(Location addressLocation) {
        this.addressLocation = addressLocation;
    }

    @Override
    public String getUri() {
        return address;
    }

    @Override
    public String describe(int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(getLocation().describe());
        sb.append("  DebugExporterItem");
        sb.append("\n");

        sb.append(super.describe(level+1));

        sb.append(getDescriptionPrefix(level + 1));
        sb.append(addressLocation.describe());
        sb.append("  address: ");
        sb.append(getAddress());
        sb.append("\n");

        return sb.toString();
    }

}

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
}

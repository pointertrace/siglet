package com.siglet.config.item;

public class DebugReceiverItem extends ReceiverItem {

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String getUri() {
        return address;
    }
}

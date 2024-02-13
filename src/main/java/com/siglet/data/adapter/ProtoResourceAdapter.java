package com.siglet.data.adapter;

import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter {

    private Resource protoResource;

    private Resource.Builder protoResourceBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoResourceAdapter(Resource protoResource) {
        this.protoResource = protoResource;
    }

    public ProtoAttributesAdapter getProtoAttributesAdapter() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoResource.getAttributesList());
        }
        return protoAttributesAdapter;
    }

    public int getDroppedAttributesCount() {
        if (protoResourceBuilder ==  null) {
            return protoResource.getDroppedAttributesCount();
        } else {
           return protoResourceBuilder.getDroppedAttributesCount();
        }
    }

    public void setDroppedAttributesCount(int droppedAttributesCount) {
        if (protoResourceBuilder == null) {
            protoResourceBuilder = protoResource.toBuilder();
        }
        protoResourceBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }








}

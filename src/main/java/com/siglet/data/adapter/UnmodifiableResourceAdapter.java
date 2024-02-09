package com.siglet.data.adapter;

import io.opentelemetry.proto.resource.v1.Resource;

public class UnmodifiableResourceAdapter {

    private final Resource protoResource;

    public UnmodifiableResourceAdapter(Resource protoResource) {
        this.protoResource = protoResource;
    }

    public int getDroppedAttributesCount() {
        return protoResource.getDroppedAttributesCount();
    }

}

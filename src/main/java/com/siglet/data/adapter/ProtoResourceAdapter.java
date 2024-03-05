package com.siglet.data.adapter;

import com.siglet.data.modifiable.ModifiableResource;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter implements ModifiableResource {

    private Resource protoResource;

    private boolean updatable;

    private Resource.Builder protoResourceBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoResourceAdapter(Resource protoResource, boolean updatable) {
        this.protoResource = protoResource;
        this.updatable = updatable;
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoResource.getAttributesList(), updatable);
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
        checkAndPrepareUpdate();
        if (protoResourceBuilder == null) {
            protoResourceBuilder = protoResource.toBuilder();
        }
        protoResourceBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }


    private void checkAndPrepareUpdate() {
        if (! updatable) {
            throw new IllegalStateException("trying to change a non updatable span");
        }
        if (protoResourceBuilder== null) {
            protoResourceBuilder = protoResource.toBuilder();
        }
    }

}

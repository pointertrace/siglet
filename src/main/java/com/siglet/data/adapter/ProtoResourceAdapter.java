package com.siglet.data.adapter;

import com.siglet.SigletError;
import com.siglet.data.modifiable.ModifiableResource;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter implements ModifiableResource {

    private final Resource protoResource;

    private final boolean updatable;

    private boolean updated;

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
        if (protoResourceBuilder == null) {
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

    public boolean isUpdated() {
        return updated || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ;
    }

    public Resource getUpdated() {
        if (!updatable) {
            return protoResource;
        } else if (!updated && (protoAttributesAdapter == null || !protoAttributesAdapter.isUpdated())) {
            return protoResource;
        } else {
            Resource.Builder bld = protoResourceBuilder != null ? protoResourceBuilder : protoResource.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            return bld.build();
        }
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable span");
        }
        if (protoResourceBuilder == null) {
            protoResourceBuilder = protoResource.toBuilder();
        }
        updated = true;
    }

}

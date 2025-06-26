package com.siglet.container.adapter.common;

import com.siglet.api.modifiable.ModifiableResource;
import com.siglet.container.adapter.Adapter;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter extends Adapter<Resource, Resource.Builder> implements ModifiableResource {

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoResourceAdapter(Resource protoResource) {
        super(protoResource, Resource::toBuilder, Resource.Builder::build);
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList());
        }
        return protoAttributesAdapter;
    }

    public int getDroppedAttributesCount() {
        return getValue(Resource::getDroppedAttributesCount,Resource.Builder::getDroppedAttributesCount);
    }

    public ProtoResourceAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Resource.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ;
    }

    @Override
    protected void enrich(Resource.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
    }

}

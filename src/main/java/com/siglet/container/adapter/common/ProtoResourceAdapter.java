package com.siglet.container.adapter.common;

import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter extends Adapter<Resource, Resource.Builder> implements com.siglet.api.data.Resource {


    public ProtoResourceAdapter() {
        super(AdapterConfig.RESOURCE_ADAPTER_CONFIG);

        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
    }

    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                getMessage()::getAttributesList);
    }

    public int getDroppedAttributesCount() {
        return getValue(Resource::getDroppedAttributesCount,Resource.Builder::getDroppedAttributesCount);
    }

    public ProtoResourceAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Resource.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

}

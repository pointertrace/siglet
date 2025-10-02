package io.github.pointertrace.siglet.impl.adapter.common;

import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;

public class ProtoResourceAdapter extends Adapter<Resource, Resource.Builder> implements io.github.pointertrace.siglet.api.signal.Resource {


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

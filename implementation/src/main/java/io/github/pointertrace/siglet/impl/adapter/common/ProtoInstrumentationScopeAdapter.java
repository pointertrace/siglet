package io.github.pointertrace.siglet.impl.adapter.common;

import io.github.pointertrace.siglet.impl.adapter.Adapter;
import io.github.pointertrace.siglet.impl.adapter.AdapterConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;

public class ProtoInstrumentationScopeAdapter extends Adapter<InstrumentationScope, InstrumentationScope.Builder>
        implements io.github.pointertrace.siglet.api.signal.InstrumentationScope {


    public ProtoInstrumentationScopeAdapter(){
        super(AdapterConfig.SCOPE_ADAPTER_CONFIG);

        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
    }

    public String getName() {
        return getValue(InstrumentationScope::getName, InstrumentationScope.Builder::getName);
    }

    public ProtoInstrumentationScopeAdapter setName(String name) {
        setValue(InstrumentationScope.Builder::setName, name);
        return this;
    }


    public String getVersion() {
        return getValue(InstrumentationScope::getVersion, InstrumentationScope.Builder::getVersion);
    }

    public ProtoInstrumentationScopeAdapter setVersion(String version) {
        setValue(InstrumentationScope.Builder::setVersion, version);
        return this;
    }


    public int getDroppedAttributesCount() {
        return getValue(InstrumentationScope::getDroppedAttributesCount,
                InstrumentationScope.Builder::getDroppedAttributesCount);
    }

    public ProtoInstrumentationScopeAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(InstrumentationScope.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                getMessage()::getAttributesList);
    }


}

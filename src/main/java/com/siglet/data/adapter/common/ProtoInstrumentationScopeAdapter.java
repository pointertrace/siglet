package com.siglet.data.adapter.common;

import com.siglet.data.adapter.Adapter;
import com.siglet.data.modifiable.ModifiableInstrumentationScope;
import io.opentelemetry.proto.common.v1.InstrumentationScope;

public class ProtoInstrumentationScopeAdapter extends Adapter<InstrumentationScope, InstrumentationScope.Builder>
        implements ModifiableInstrumentationScope {

    private ProtoAttributesAdapter protoAttributesAdapter;

    public ProtoInstrumentationScopeAdapter(InstrumentationScope protoInstrumentationScope, boolean updatable) {
        super(protoInstrumentationScope, InstrumentationScope::toBuilder,
                InstrumentationScope.Builder::build, updatable);
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
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList(), isUpdatable());
        }
        return protoAttributesAdapter;
    }

    @Override
    protected void enrich(InstrumentationScope.Builder builder) {
        if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
    }

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated());

    }

}

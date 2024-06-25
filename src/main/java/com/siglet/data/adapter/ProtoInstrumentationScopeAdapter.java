package com.siglet.data.adapter;

import com.siglet.SigletError;
import com.siglet.data.modifiable.ModifiableInstrumentationScope;
import io.opentelemetry.proto.common.v1.InstrumentationScope;

public class ProtoInstrumentationScopeAdapter implements ModifiableInstrumentationScope {

    private final InstrumentationScope protoInstrumentationScope;

    private InstrumentationScope.Builder protoInstrumentationScopeBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private final boolean updatable;

    private boolean updated;

    public ProtoInstrumentationScopeAdapter(InstrumentationScope protoInstrumentationScope, boolean updatable) {
        this.protoInstrumentationScope = protoInstrumentationScope;
        this.updatable = updatable;
    }


    public String getName() {
        return protoInstrumentationScopeBuilder == null ?
                protoInstrumentationScope.getName() : protoInstrumentationScopeBuilder.getName();
    }

    public void setName(String name) {
        checkAndPrepareUpdate();
        protoInstrumentationScopeBuilder.setName(name);
    }


    public String getVersion() {
        return protoInstrumentationScopeBuilder == null ?
                protoInstrumentationScope.getVersion() : protoInstrumentationScopeBuilder.getVersion();
    }

    public void setVersion(String version) {
        checkAndPrepareUpdate();
        protoInstrumentationScopeBuilder.setVersion(version);
    }


    public int getDroppedAttributesCount() {
        return protoInstrumentationScopeBuilder == null ?
                protoInstrumentationScope.getDroppedAttributesCount() :
                protoInstrumentationScopeBuilder.getDroppedAttributesCount();
    }

    public void setDroppedAttributesCount(int droppedAttributesCount) {
        checkAndPrepareUpdate();
        protoInstrumentationScopeBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }

    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoInstrumentationScope.getAttributesList(), updatable);
        }
        return protoAttributesAdapter;
    }

    public boolean isUpdated() {
        return updated || (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) ;

    }

    public InstrumentationScope getUpdated() {
        if (!updatable) {
            return protoInstrumentationScope;
        } else if (!updated && (protoAttributesAdapter == null || !protoAttributesAdapter.isUpdated())) {
            return protoInstrumentationScope;
        } else {
            InstrumentationScope.Builder bld = protoInstrumentationScopeBuilder != null ?
                    protoInstrumentationScopeBuilder : protoInstrumentationScope.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            return bld.build();
        }
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable event list!");
        }
        if (protoInstrumentationScopeBuilder == null) {
            protoInstrumentationScopeBuilder = protoInstrumentationScope.toBuilder();
        }
        updated = true;
    }
}

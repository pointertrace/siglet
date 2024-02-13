package com.siglet.data.adapter;

import io.opentelemetry.proto.common.v1.InstrumentationScope;

public class ProtoInstrumentationScopeAdapter {

    private InstrumentationScope protoInstrumentationScope;

    private InstrumentationScope.Builder protoInstrumentationScopeBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;


    public String getName() {
        return AdapterUtils.read(protoInstrumentationScope, protoInstrumentationScope::getName, protoInstrumentationScope,
                protoInstrumentationScopeBuilder::getName);
    }

    public void setName(String name) {
        AdapterUtils.write(protoInstrumentationScopeBuilder, this::createBuilder,
                protoInstrumentationScopeBuilder::setName, name);
    }

    public ProtoAttributesAdapter getProtoAttributesAdapter() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoInstrumentationScope.getAttributesList());
        }
        return protoAttributesAdapter;
    }

    protected void createBuilder() {
        protoInstrumentationScopeBuilder = InstrumentationScope.newBuilder();
    }
}

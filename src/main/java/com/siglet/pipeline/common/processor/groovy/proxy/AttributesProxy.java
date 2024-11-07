package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.SigletError;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;

public abstract class AttributesProxy extends BaseProxy{

    private final ProtoAttributesAdapter attributesAdapter;

    public AttributesProxy(Object thisSignal,ProtoAttributesAdapter attributesAdapter) {
        super(thisSignal);
        this.attributesAdapter = attributesAdapter;
    }

    public void methodMissing(String name, Object args) {
        // todo chegar o tipo do args
        attributesAdapter.putAt(name, ((Object[]) args)[0]);
    }

    public void propertyMissing(String name) {
        throw new SigletError("Property missing [" + name + "] should not be called!");
    }

    public void remove(String key) {
        attributesAdapter.remove(key);
    }

    public ProtoAttributesAdapter getAttributes() {
        return attributesAdapter;
    }

}

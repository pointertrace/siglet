package io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.container.adapter.common.ProtoAttributesAdapter;

public abstract class AttributesProxy extends BaseProxy {

    private final ProtoAttributesAdapter attributesAdapter;

    protected AttributesProxy(Signal signal, ProtoAttributesAdapter attributesAdapter) {
        super(signal);
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

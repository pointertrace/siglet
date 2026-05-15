package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.AttributesAdapter;

public abstract class AttributesProxy extends BaseProxy {

    private final AttributesAdapter attributesAdapter;

    protected AttributesProxy(Signal signal, AttributesAdapter attributesAdapter) {
        super(signal);
        this.attributesAdapter = attributesAdapter;
    }

    public void methodMissing(String name, Object args) {
        attributesAdapter.putAt(name, ((Object[]) args)[0]);
    }

    public void propertyMissing(String name) {
        throw new SigletError("Property missing [" + name + "] should not be called!");
    }

    public void remove(String key) {
        attributesAdapter.remove(key);
    }

    public AttributesAdapter getAttributes() {
        return attributesAdapter;
    }

}

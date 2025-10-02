package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.trace.ProtoSpanAdapter;

public class SpanAttributesProxy extends AttributesProxy {

    private final ProtoSpanAdapter protoSpanAdapter;

    public SpanAttributesProxy(Signal signal, ProtoSpanAdapter protoSpanAdapter) {
        super(signal, protoSpanAdapter.getAttributes());
        this.protoSpanAdapter = protoSpanAdapter;
    }

    public ProtoSpanAdapter getSpan() {
        return protoSpanAdapter;
    }

}

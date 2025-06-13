package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;

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

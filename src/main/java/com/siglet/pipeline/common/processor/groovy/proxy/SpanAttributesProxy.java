package com.siglet.pipeline.common.processor.groovy.proxy;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;

public class SpanAttributesProxy extends AttributesProxy {

    private final ProtoSpanAdapter protoSpanAdapter;

    public SpanAttributesProxy(Object thisSignal, ProtoSpanAdapter protoSpanAdapter) {
        super(thisSignal, protoSpanAdapter.getAttributes());
        this.protoSpanAdapter = protoSpanAdapter;
    }

    public ProtoSpanAdapter getSpan() {
        return protoSpanAdapter;
    }

}

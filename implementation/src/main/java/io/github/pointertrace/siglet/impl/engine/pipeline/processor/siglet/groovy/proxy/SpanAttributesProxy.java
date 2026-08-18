package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;

public class SpanAttributesProxy extends AttributesProxy {

    private final SpanAdapter protoSpanAdapter;

    public SpanAttributesProxy(Signal signal, SpanAdapter spanAdapter) {
        super(signal, spanAdapter.getAttributes());
        this.protoSpanAdapter = spanAdapter;
    }

    public SpanAdapter getSpan() {
        return protoSpanAdapter;
    }

}

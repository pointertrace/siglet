package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.api.trace.StatusCode;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;

public class SpanStatusProxy extends BaseProxy {

    private final ProtoSpanAdapter spanAdapter;

    public SpanStatusProxy(Signal signal, ProtoSpanAdapter spanAdapter) {
        super(signal);
        this.spanAdapter = spanAdapter;
    }

    public void code(String code) {
        spanAdapter.getStatus().setCode(StatusCode.valueOf(code));
    }

    public void message(String message) {
        spanAdapter.getStatus().setStatusMessage(message);
    }

    public ProtoSpanAdapter getSpan(){
        return spanAdapter;
    }

}

package com.siglet.pipeline.processor.common.action.groovy.proxy;

import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.trace.StatusCode;

public class SpanStatusProxy extends BaseProxy {

    private final ProtoSpanAdapter spanAdapter;

    public SpanStatusProxy(Object thisSignal, ProtoSpanAdapter spanAdapter) {
        super(thisSignal);
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

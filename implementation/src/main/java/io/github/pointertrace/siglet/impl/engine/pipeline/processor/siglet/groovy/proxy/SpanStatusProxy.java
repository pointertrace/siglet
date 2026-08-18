package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.proxy;

import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.api.signal.trace.StatusCode;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;

public class SpanStatusProxy extends BaseProxy {

    private final SpanAdapter spanAdapter;

    public SpanStatusProxy(Signal signal, SpanAdapter spanAdapter) {
        super(signal);
        this.spanAdapter = spanAdapter;
    }

    public void code(String code) {
        spanAdapter.getStatus().setCode(StatusCode.valueOf(code));
    }

    public void message(String message) {
        spanAdapter.getStatus().setStatusMessage(message);
    }

    public SpanAdapter getSpan(){
        return spanAdapter;
    }

}

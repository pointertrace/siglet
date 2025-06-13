package com.siglet.container.engine.pipeline.processor.groovy.proxy;

import com.siglet.api.Signal;
import com.siglet.api.trace.SpanKind;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import groovy.lang.Closure;

public class SpanProxy extends BaseProxy {

    private final ProtoSpanAdapter spanAdapter;

    public SpanProxy(Signal signal, ProtoSpanAdapter spanAdapter) {
        super(signal);
        this.spanAdapter = spanAdapter;
    }

    public void name(String name) {
        spanAdapter.setName(name);
    }

    public void spanId(Long spanId) {
        spanAdapter.setSpanId(spanId);
    }

    public void traceId(long high, long low) {
        spanAdapter.setTraceId(high, low);
    }

    public void kind(String kind) {
        spanAdapter.setKind(SpanKind.valueOf(kind));
    }

    public void droppedEventsCount(int droppedEventsCount) {
        spanAdapter.setDroppedEventsCount(droppedEventsCount);
    }

    public void droppedLinksCount(int droppedLinksCount) {
        spanAdapter.setDroppedLinksCount(droppedLinksCount);
    }

    public void droppedAttributesCount(int droppedAttributesCount) {
        spanAdapter.setDroppedAttributesCount(droppedAttributesCount);
    }

    public void flags(int flags) {
        spanAdapter.setFlags(flags);
    }

    public void startTimeUnixNano(long startTimeUnixNano) {
        spanAdapter.setStartTimeUnixNano(startTimeUnixNano);
    }

    public void endTimeUnixNano(long endTimeUnixNano) {
        spanAdapter.setEndTimeUnixNano(endTimeUnixNano);
    }

    public void status(Closure<Void> closure) {
        closure.setDelegate(new SpanStatusProxy(getSignal(), spanAdapter));
        closure.setResolveStrategy(Closure.DELEGATE_ONLY);
        closure.call();
    }

    public void attributes(Closure<Void> closure) {
        closure.setDelegate(new SpanAttributesProxy(getSignal(), spanAdapter));
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

}

package com.siglet.data.adapter.trace;

import com.siglet.SigletError;
import com.siglet.data.Clonable;
import com.siglet.data.modifiable.trace.ModifiableSpan;
import com.siglet.data.modifiable.trace.ModifiableTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ProtoTraceAdapter implements ModifiableTrace<ProtoSpanAdapter>, Clonable {

    private final boolean updatable;

    private final ProtoSpanAdapter firstSpan;

    private final Map<Long, ProtoSpanAdapter> spansBySpanId = new HashMap<>();

    private final List<ProtoSpanAdapter> spans = new ArrayList<>();

    public ProtoTraceAdapter(ProtoSpanAdapter firstSpan, boolean updatable) {
        this.firstSpan = firstSpan;
        this.spansBySpanId.put(firstSpan.getSpanId(), firstSpan);
        this.spans.add(firstSpan);
        this.updatable = updatable;
    }

    @Override
    public long getTraceIdHigh() {
        return firstSpan.getTraceIdHigh();
    }

    @Override
    public long getTraceIdLow() {
        return firstSpan.getTraceIdLow();
    }

    @Override
    public byte[] getTraceId() {
        return firstSpan.getTraceId();
    }

    @Override
    public String getTraceIdEx() {
        return Long.toHexString(getTraceIdHigh()) + Long.toHexString(getTraceIdLow());
    }

    @Override
    public ProtoTraceAdapter add(ProtoSpanAdapter span) {
        checkUpdate();
        spansBySpanId.put(span.getSpanId(), span);
        spans.add(span);
        return this;
    }

    @Override
    public boolean remove(long spanId) {
        checkUpdate();
        return spansBySpanId.remove(spanId) != null;
    }

    @Override
    public ProtoSpanAdapter get(long spanId) {
        return spansBySpanId.get(spanId);
    }

    @Override
    public ProtoSpanAdapter getAt(int index) {
        return spans.get(index);
    }

    @Override
    public boolean isComplete() {
        return hasRoot() && !hasOrphan();
    }

    @Override
    public int getSize() {
        return spansBySpanId.size();
    }

    public boolean hasRoot() {
        return spansBySpanId.values().stream().anyMatch(span -> span.getParentSpanId() == 0);
    }

    private boolean hasOrphan() {
        // TODO implementar!!!!
        throw new IllegalStateException("to be implemented");
//        return spansBySpanId.values().stream()
//                .flatMap(span -> span.getLinks().stream())
//                .anyMatch(link -> spansBySpanId.get(link.getSpanId()) == null);
    }

    private void checkUpdate() {
        if (!updatable) {
            throw new SigletError("trying to change a non updatable trace");
        }
    }

    @Override
    public ProtoTraceAdapter clone() {
        ProtoTraceAdapter result = new ProtoTraceAdapter(firstSpan, updatable);
        spansBySpanId.values().forEach(result::add);
        return result;
    }

    public void forEachSpan(Consumer<ModifiableSpan> spanConsumer) {
        spans.forEach(spanConsumer);
    }
}
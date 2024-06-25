package com.siglet.data.adapter.trace;

import com.siglet.SigletError;
import com.siglet.data.Clonable;
import com.siglet.data.modifiable.trace.ModifiableSpan;
import com.siglet.data.modifiable.trace.ModifiableTrace;
import com.siglet.data.unmodifiable.trace.UnmodifiableSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ProtoTraceAdapter implements ModifiableTrace, Clonable {

    private final boolean updatable;

    private final ModifiableSpan firstSpan;

    private final Map<Long, ModifiableSpan> spansBySpanId = new HashMap<>();

    private final List<ModifiableSpan> spans = new ArrayList<>();

    public ProtoTraceAdapter(ModifiableSpan firstSpan, boolean updatable) {
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
    public void add(ModifiableSpan span) {
        checkUpdate();
        spansBySpanId.put(span.getSpanId(), span);
        spans.add(span);
    }

    @Override
    public boolean remove(long spanId) {
        checkUpdate();
        return spansBySpanId.remove(spanId) != null;
    }

    @Override
    public ModifiableSpan get(long spanId) {
        return spansBySpanId.get(spanId);
    }

    @Override
    public UnmodifiableSpan getAt(int index) {
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


    private boolean hasRoot() {
        return spansBySpanId.values().stream().anyMatch(span -> span.getLinks().size() == 0);
    }

    private boolean hasOrphan() {
        return spansBySpanId.values().stream()
                .flatMap(span -> span.getLinks().stream())
                .anyMatch(link -> spansBySpanId.get(link.getSpanId()) == null);
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
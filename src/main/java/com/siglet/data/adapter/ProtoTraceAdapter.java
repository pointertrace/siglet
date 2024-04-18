package com.siglet.data.adapter;

import com.siglet.data.Clonable;
import com.siglet.data.modifiable.ModifiableSpan;
import com.siglet.data.modifiable.ModifiableTrace;

import java.util.HashMap;
import java.util.Map;

public class ProtoTraceAdapter implements ModifiableTrace, Clonable {

    private final boolean updatable;

    private final ModifiableSpan firstSpan;

    private final Map<Long, ModifiableSpan> spans = new HashMap<>();

    public ProtoTraceAdapter(ModifiableSpan firstSpan, boolean updatable) {
        this.firstSpan = firstSpan;
        this.spans.put(firstSpan.getSpanId(), firstSpan);
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
        spans.put(span.getSpanId(), span);
    }

    @Override
    public boolean remove(long spanId) {
        checkUpdate();
        return spans.remove(spanId) != null;
    }

    @Override
    public ModifiableSpan get(long spanId) {
        return spans.get(spanId);
    }

    @Override
    public boolean isComplete() {
        return hasRoot() && !hasOrphan();
    }

    @Override
    public int getSize() {
        return spans.size();
    }


    private boolean hasRoot() {
        return spans.values().stream().anyMatch(span -> span.getLinks().size() == 0);
    }

    private boolean hasOrphan() {
        return spans.values().stream()
                .flatMap(span -> span.getLinks().stream())
                .anyMatch(link -> spans.get(link.getSpanId()) == null);
    }

    private void checkUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable trace");
        }
    }

    @Override
    public ProtoTraceAdapter clone() {
        ProtoTraceAdapter result = new ProtoTraceAdapter(firstSpan, updatable);
        spans.values().forEach(result::add);
        return result;
    }
}
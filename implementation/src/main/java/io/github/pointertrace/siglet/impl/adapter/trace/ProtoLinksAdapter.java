package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.Links;
import io.github.pointertrace.siglet.impl.adapter.AdapterList;
import io.github.pointertrace.siglet.impl.adapter.AdapterListConfig;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.common.ProtoAttributesAdapter;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.Map;

public class ProtoLinksAdapter extends AdapterList<Span.Link, Span.Link.Builder, ProtoLinkAdapter>
        implements Links {


    public ProtoLinksAdapter() {
        super(AdapterListConfig.LINKS_ADAPTER_CONFIG);
    }

    // TODO simplificar
    public boolean has(long traceIdHigh, long traceIdLow, long spanId) {
        return findIndex(m -> traceIdHigh == AdapterUtils.traceIdHigh(m.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(m.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(m.getSpanId().toByteArray()),
                b -> traceIdHigh == AdapterUtils.traceIdHigh(b.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(b.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(b.getSpanId().toByteArray())) != -1;
    }

    public ProtoLinkAdapter get(long traceIdHigh, long traceIdLow, int spanId) {
        int idx = findIndex(m -> traceIdHigh == AdapterUtils.traceIdHigh(m.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(m.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(m.getSpanId().toByteArray()),
                b -> traceIdHigh == AdapterUtils.traceIdHigh(b.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(b.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(b.getSpanId().toByteArray()));
        if (idx >= 0) {
            return get(idx);
        } else {
            return null;
        }
    }


    public ProtoLinkAdapter add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes) {
        ProtoLinkAdapter newAdapter = super.add();
        newAdapter.setTraceId(traceIdHigh, traceIdLow);
        newAdapter.setSpanId(spanId);
        newAdapter.setTraceState(traceState);
        ProtoAttributesAdapter attributesAdapter = newAdapter.getAttributes();
        attributes.forEach(attributesAdapter::putAt);

        return newAdapter;
    }

    public boolean remove(long traceIdHigh, long traceIdLow, int spanId) {
        return remove(m -> traceIdHigh == AdapterUtils.traceIdHigh(m.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(m.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(m.getSpanId().toByteArray()),
                b -> traceIdHigh == AdapterUtils.traceIdHigh(b.getTraceId().toByteArray()) &&
                        traceIdLow == AdapterUtils.traceIdLow(b.getTraceId().toByteArray()) &&
                        spanId == AdapterUtils.spanId(b.getSpanId().toByteArray()));
    }

}

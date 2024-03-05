package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import com.siglet.data.modifiable.ModifiableLinks;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtoLinksAdapter implements ModifiableLinks {

    private List<ProtoLinkAdapter> linksAdapter;

    private boolean updatable;


    public ProtoLinksAdapter(List<Span.Link> linksProto, boolean updatable) {
        linksAdapter = new ArrayList<>(linksProto.size());
        linksProto.forEach(lk -> linksAdapter.add(new ProtoLinkAdapter(lk, updatable)));
        this.updatable = updatable;
    }

    public boolean has(long traceIdHigh, long traceIdLow, long spanId) {
        for (ProtoLinkAdapter protoLinkAdapter : linksAdapter) {
            if (protoLinkAdapter.getTraceIdHigh() == traceIdHigh && protoLinkAdapter.getTraceIdLow() == traceIdLow &&
                    protoLinkAdapter.getSpanId() == spanId) {
                return true;
            }
        }
        return false;

    }

    public ProtoLinkAdapter get(long traceIdHigh, long traceIdLow, int spanId) {
        for (ProtoLinkAdapter protoLinkAdapter : linksAdapter) {
            if (protoLinkAdapter.getTraceIdHigh() == traceIdHigh && protoLinkAdapter.getTraceIdLow() == traceIdLow &&
                    protoLinkAdapter.getSpanId() == spanId) {
                return protoLinkAdapter;
            }
        }
        return null;
    }

    public void add(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String, Object> attributes) {
        checkUpdate();
        Span.Link linkProto = Span.Link.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(traceIdHigh, traceIdLow)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(spanId)))
                .setTraceState(traceState)
                .addAllAttributes(AdapterUtils.mapToKeyValueList(attributes))
                .build();

        linksAdapter.add(new ProtoLinkAdapter(linkProto, updatable));

    }

    public boolean remove(long traceIdHigh, long traceIdLow, int spanId) {
        checkUpdate();
        return linksAdapter.removeIf(
                la -> la.getTraceIdHigh() == traceIdHigh &&
                        la.getTraceIdLow() == traceIdLow &&
                        la.getSpanId() == spanId);
    }

    public int size() {
        return linksAdapter.size();
    }

    private void checkUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable link list!");
        }
    }
}

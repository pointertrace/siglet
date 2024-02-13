package com.siglet.data.adapter;

import com.google.protobuf.ByteString;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.TraceId;
import io.opentelemetry.proto.trace.v1.Span;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtoLinksAdapter {

   private List<Span.Link> linksProto;

   private List<ProtoLinkAdapter> linksAdapter;


    public ProtoLinksAdapter(List<Span.Link> linksProto) {
        this.linksProto = linksProto;
    }

    public ProtoLinkAdapter getLink(String traceId, String spanId) {
        convertIfNeeded();
        for(ProtoLinkAdapter protoLinkAdapter: linksAdapter) {
            if (protoLinkAdapter.getTraceId().equals(traceId) && protoLinkAdapter.getSpanId().equals(spanId)) {
                return protoLinkAdapter;
            }
        }
        return null;
    }

    public void addLink(long traceIdHigh, long traceIdLow, long spanId, String traceState, Map<String,Object> attributes) {
        Span.Link linkProto = Span.Link.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(traceIdHigh, traceIdLow)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(spanId)))
                .setTraceState(traceState)
                .addAllAttributes(AdapterUtils.mapToKeyValueList(attributes))
                .build();

        linksProto.add(linkProto);
        linksAdapter.add(new ProtoLinkAdapter(linkProto));

    }

    private void convertIfNeeded() {
        if (linksAdapter == null) {
            linksAdapter = new ArrayList<>(linksProto.size());
            for(Span.Link linkProto: linksProto) {
                linksAdapter.add(new ProtoLinkAdapter(linkProto));
            }
        }
    }


}

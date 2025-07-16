package com.siglet.container.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.api.data.trace.SpanKind;
import com.siglet.container.adapter.Adapter;
import com.siglet.container.adapter.AdapterConfig;
import com.siglet.container.adapter.AdapterListConfig;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.common.ProtoEventsAdapter;
import com.siglet.container.adapter.common.ProtoInstrumentationScopeAdapter;
import com.siglet.container.adapter.common.ProtoResourceAdapter;
import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.TraceId;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ProtoSpanAdapter extends Adapter<Span, Span.Builder>
        implements com.siglet.api.data.trace.Span {

    private Resource protoResource;

    private ProtoResourceAdapter protoResourceAdapter;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    public ProtoSpanAdapter() {
        super(AdapterConfig.SPAN_ADAPTER_CONFIG);
        addEnricher(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG, attributes -> {
            getBuilder().clearAttributes();
            getBuilder().addAllAttributes((Iterable<KeyValue>) attributes);
        });
        addEnricher(AdapterListConfig.LINKS_ADAPTER_CONFIG, links -> {
            getBuilder().clearLinks();
            getBuilder().addAllLinks((Iterable<Span.Link>) links);
        });
        addEnricher(AdapterListConfig.EVENTS_ADAPTER_CONFIG, events -> {
            getBuilder().clearEvents();
            getBuilder().addAllEvents((Iterable<Span.Event>) events);
        });
        addEnricher(AdapterConfig.STATUS_ADAPTER_CONFIG, status -> {
            getBuilder().setStatus((Status) status);
        });

    }

    public ProtoSpanAdapter recycle(Span protoSpan, Resource protoResource,
                                    InstrumentationScope protoInstrumentationScope) {
        super.recycle(protoSpan);
        this.protoResource = protoResource;
        if (protoResourceAdapter != null) {
            protoResourceAdapter.recycle(protoResource);
        }
        this.protoInstrumentationScope = protoInstrumentationScope;
        if (protoInstrumentationScopeAdapter != null) {
            protoInstrumentationScopeAdapter.recycle(protoInstrumentationScope);
        }
        return this;
    }

    public ProtoSpanAdapter recycle() {
        recycle(Span.newBuilder());
        return this;
    }

    @Override
    public long getTraceIdHigh() {
        ByteString traceId = getValue(Span::getTraceId, Span.Builder::getTraceId);
        if (traceId == null || traceId.isEmpty()) {
            return 0;
        } else {
            return ByteBuffer.wrap(
                            Arrays.copyOfRange(traceId.toByteArray(), 0, 8))
                    .getLong();
        }
    }

    @Override
    public long getTraceIdLow() {
        ByteString traceId = getValue(Span::getTraceId, Span.Builder::getTraceId);
        if (traceId == null || traceId.isEmpty()) {
            return 0;
        } else {
            return ByteBuffer.wrap(
                    Arrays.copyOfRange(traceId.toByteArray(), 8, 16)
            ).getLong();
        }
    }

    @Override
    public byte[] getTraceId() {
        ByteString traceId = getValue(Span::getTraceId, Span.Builder::getTraceId);
        return traceId == null ? null : traceId.toByteArray();
    }

    @Override
    public ProtoSpanAdapter setTraceId(long high, long low) {
        setValue(Span.Builder::setTraceId, ByteString.copyFrom(
                ByteBuffer.allocate(Long.BYTES * 2).putLong(high).putLong(low).array()));
        return this;
    }

    @Override
    public ProtoSpanAdapter setTraceId(byte[] traceId) {
        setValue(Span.Builder::setTraceId, ByteString.copyFrom(traceId));
        return this;
    }

    @Override
    public long getSpanId() {
        ByteString spanId = getValue(Span::getSpanId, Span.Builder::getSpanId);
        if (spanId == null || spanId.isEmpty()) {
            return 0;
        } else {
            return ByteBuffer.wrap(spanId.toByteArray()).getLong();
        }
    }

    @Override
    public ProtoSpanAdapter setSpanId(long spanId) {
        setValue(Span.Builder::setSpanId, ByteString.copyFrom(ByteBuffer.allocate(Long.BYTES).putLong(spanId).array()));
        return this;
    }

    @Override
    public long getParentSpanId() {
        ByteString parentSpanId = getValue(Span::getParentSpanId, Span.Builder::getParentSpanId);
        if (parentSpanId == null || parentSpanId.isEmpty()) {
            return 0;
        } else {
            return ByteBuffer.wrap(parentSpanId.toByteArray()).getLong();
        }
    }

    @Override
    public String getSpanIdEx() {
        return SpanId.fromLong(getSpanId());
    }

    @Override
    public String getTraceIdEx() {
        return TraceId.fromBytes(getTraceId());
    }

    @Override
    public String getParentSpanIdEx() {
        return Long.toHexString(getParentSpanId());
    }

    @Override
    public ProtoSpanAdapter setParentSpanId(long parentSpanId) {
        setValue(Span.Builder::setParentSpanId,
                ByteString.copyFrom(ByteBuffer.allocate(Long.BYTES).putLong(parentSpanId).array()));
        return this;
    }

    @Override
    public String getTraceState() {
        return getValue(Span::getTraceState, Span.Builder::getTraceState);
    }

    @Override
    public ProtoSpanAdapter setTraceState(String traceState) {
        setValue(Span.Builder::setTraceState, traceState);
        return this;
    }

    @Override
    public String getName() {
        return getValue(Span::getName, Span.Builder::getName);
    }

    @Override
    public ProtoSpanAdapter setName(String name) {
        setValue(Span.Builder::setName, name);
        return this;
    }

    @Override
    public long getStartTimeUnixNano() {
        return getValue(Span::getStartTimeUnixNano, Span.Builder::getStartTimeUnixNano);
    }

    @Override
    public long getEndTimeUnixNano() {
        return getValue(Span::getEndTimeUnixNano, Span.Builder::getEndTimeUnixNano);
    }

    @Override
    public SpanKind getKind() {
        return AdapterUtils.getKind(getValue(Span::getKind, Span.Builder::getKind));
    }

    @Override
    public ProtoSpanAdapter setKind(SpanKind spanKind) {
        setValue(Span.Builder::setKind, AdapterUtils.getKind(spanKind));
        return this;
    }

    protected List<KeyValue> getAttributeList() {
        return getValue(Span::getAttributesList, Span.Builder::getAttributesList);
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        return getAdapterList(AdapterListConfig.ATTRIBUTES_ADAPTER_CONFIG,
                this::getAttributeList);
    }

    protected Status getStatusValue() {
        return getValue(Span::getStatus, Span.Builder::getStatus);
    }

    public ProtoStatusAdapter getStatus() {
        return (ProtoStatusAdapter) getAdapter(AdapterConfig.STATUS_ADAPTER_CONFIG, this::getStatusValue);
    }

    @Override
    public ProtoResourceAdapter getResource() {
        if (protoResourceAdapter == null) {
            protoResourceAdapter = new ProtoResourceAdapter();
            protoResourceAdapter.recycle(protoResource);
        } else if (!protoResourceAdapter.isReady()) {
            protoResourceAdapter.recycle(protoResource);
        }

        return protoResourceAdapter;
    }

    @Override
    public ProtoInstrumentationScopeAdapter getInstrumentationScope() {
        if (protoInstrumentationScopeAdapter == null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter();
            protoInstrumentationScopeAdapter.recycle(protoInstrumentationScope);
        } else if (!protoInstrumentationScopeAdapter.isReady()) {
            protoInstrumentationScopeAdapter.recycle(protoInstrumentationScope);
        }

        return protoInstrumentationScopeAdapter;
    }


    protected List<Span.Link> getLinkList() {
        return getValue(Span::getLinksList, Span.Builder::getLinksList);
    }

    @Override
    public ProtoLinksAdapter getLinks() {
        return (ProtoLinksAdapter) getAdapterList(AdapterListConfig.LINKS_ADAPTER_CONFIG, this::getLinkList);
    }

    protected List<Span.Event> getEventList() {
        return getValue(Span::getEventsList, Span.Builder::getEventsList);
    }

    @Override
    public ProtoEventsAdapter getEvents() {
        return (ProtoEventsAdapter) getAdapterList(AdapterListConfig.EVENTS_ADAPTER_CONFIG, this::getEventList);
    }

    public Span getProtoSpan() {
        return getMessage();
    }


    @Override
    public ProtoSpanAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        setValue(Span.Builder::setStartTimeUnixNano, startTimeUnixNano);
        return this;
    }

    @Override
    public ProtoSpanAdapter setEndTimeUnixNano(long endTimeUnixNano) {
        setValue(Span.Builder::setEndTimeUnixNano, endTimeUnixNano);
        return this;
    }

    @Override
    public int getFlags() {
        return getValue(Span::getFlags, Span.Builder::getFlags);
    }


    @Override
    public boolean isRoot() {
        return getParentSpanId() == 0;
    }


    @Override
    public ProtoSpanAdapter setFlags(int flags) {
        setValue(Span.Builder::setFlags, flags);
        return this;
    }

    @Override
    public int getDroppedAttributesCount() {
        return getValue(Span::getDroppedAttributesCount, Span.Builder::getDroppedAttributesCount);
    }

    @Override
    public ProtoSpanAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        setValue(Span.Builder::setDroppedAttributesCount, droppedAttributesCount);
        return this;
    }

    @Override
    public int getDroppedEventsCount() {
        return getValue(Span::getDroppedEventsCount, Span.Builder::getDroppedEventsCount);
    }

    @Override
    public ProtoSpanAdapter setDroppedEventsCount(int droppedEventsCount) {
        setValue(Span.Builder::setDroppedEventsCount, droppedEventsCount);
        return this;
    }

    @Override
    public int getDroppedLinksCount() {
        return getValue(Span::getDroppedLinksCount, Span.Builder::getDroppedLinksCount);
    }

    @Override
    public ProtoSpanAdapter setDroppedLinksCount(int droppedLinksCount) {
        setValue(Span.Builder::setDroppedLinksCount, droppedLinksCount);
        return this;
    }

    public Resource getUpdatedResource() {
        if (protoResourceAdapter != null) {
            return protoResourceAdapter.getUpdated();
        } else {
            return protoResource;
        }
    }

    public InstrumentationScope getUpdatedInstrumentationScope() {
        if (protoInstrumentationScopeAdapter != null) {
            return protoInstrumentationScopeAdapter.getUpdated();
        } else {
            return protoInstrumentationScope;
        }
    }


    @Override
    public String getId() {
        return "Span(traceId:" + getTraceIdEx() + ",spanId:" + getSpanIdEx() + ")";
    }
}

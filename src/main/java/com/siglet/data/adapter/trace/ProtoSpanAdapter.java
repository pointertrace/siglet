package com.siglet.data.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.data.Clonable;
import com.siglet.data.adapter.Adapter;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.common.ProtoEventsAdapter;
import com.siglet.data.adapter.common.ProtoInstrumentationScopeAdapter;
import com.siglet.data.adapter.common.ProtoResourceAdapter;
import com.siglet.data.modifiable.trace.ModifiableSpan;
import com.siglet.data.trace.SpanKind;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ProtoSpanAdapter extends Adapter<Span, Span.Builder> implements ModifiableSpan, Clonable {

    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoStatusAdapter protoStatusAdapter;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoLinksAdapter protoLinksAdapter;

    private ProtoEventsAdapter protoEventsAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;

    public ProtoSpanAdapter(Span protoSpan, Resource protoResource, InstrumentationScope protoInstrumentationScope,
                            boolean updatable) {
        super(protoSpan, Span::toBuilder, Span.Builder::build, updatable);
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
    }

    public ProtoSpanAdapter() {
        super(Span.newBuilder(), Span.Builder::build);
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
        return Long.toHexString(getSpanId());
    }

    @Override
    public String getTraceIdEx() {
        return Long.toHexString(getTraceIdLow()) + Long.toHexString(getTraceIdHigh());
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

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(getMessage().getAttributesList(), isUpdatable());
        }
        return protoAttributesAdapter;
    }

    public ProtoStatusAdapter getStatus() {
        if (protoStatusAdapter == null) {
            protoStatusAdapter = new ProtoStatusAdapter(getMessage().getStatus(), isUpdatable());
        }
        return protoStatusAdapter;
    }

    @Override
    public ProtoResourceAdapter getResource() {
        if (protoResourceAdapter == null) {
            protoResourceAdapter = new ProtoResourceAdapter(protoResource, isUpdatable());
        }
        return protoResourceAdapter;
    }

    @Override
    public ProtoInstrumentationScopeAdapter getInstrumentationScope() {
        if (protoInstrumentationScopeAdapter == null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(protoInstrumentationScope, isUpdatable());
        }
        return protoInstrumentationScopeAdapter;
    }

    @Override
    public ProtoLinksAdapter getLinks() {
        if (protoLinksAdapter == null) {
            protoLinksAdapter = new ProtoLinksAdapter(getMessage().getLinksList(), isUpdatable());
        }
        return protoLinksAdapter;
    }

    @Override
    public ProtoEventsAdapter getEvents() {
        if (protoEventsAdapter == null) {
            protoEventsAdapter = new ProtoEventsAdapter(getMessage().getEventsList(), isUpdatable());
        }
        return protoEventsAdapter;
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

    @Override
    public boolean isUpdated() {
        return super.isUpdated() || attributesUpdated() || linksUpdated() ||
                eventsUpdated();
    }

    @Override
    protected void enrich(Span.Builder builder) {
        if (attributesUpdated()) {
            builder.clearAttributes();
            builder.addAllAttributes(protoAttributesAdapter.getUpdated());
        }
        if (linksUpdated()) {
            builder.clearLinks();
            builder.addAllLinks(protoLinksAdapter.getUpdated());
        }
        if (eventsUpdated()) {
            builder.clearEvents();
            builder.addAllEvents(protoEventsAdapter.getUpdated());
        }
    }

    @Override
    public ProtoSpanAdapter clone() {
        return new ProtoSpanAdapter(getUpdated(),
                getUpdatedResource(), getUpdatedInstrumentationScope(), isUpdatable());
    }

    private boolean attributesUpdated() {
        return protoAttributesAdapter != null && protoAttributesAdapter.isUpdated();
    }

    private boolean resourceUpdated() {
        return protoResourceAdapter != null && protoResourceAdapter.isUpdated();
    }

    private boolean linksUpdated() {
        return protoLinksAdapter != null && protoLinksAdapter.isUpdated();
    }

    private boolean eventsUpdated() {
        return protoEventsAdapter != null && protoEventsAdapter.isUpdated();
    }

    public boolean instrumentationScopeUpdated() {
        return protoInstrumentationScopeAdapter != null && protoInstrumentationScopeAdapter.isUpdated();
    }

    public Resource getUpdatedResource() {
        if (!isUpdatable()) {
            return protoResource;
        } else if (protoResourceAdapter == null || !protoResourceAdapter.isUpdated()) {
            return protoResource;
        } else {
            return protoResourceAdapter.getUpdated();
        }
    }

    public InstrumentationScope getUpdatedInstrumentationScope() {
        if (!isUpdatable()) {
            return protoInstrumentationScope;
        } else if (protoInstrumentationScopeAdapter == null || !protoInstrumentationScopeAdapter.isUpdated()) {
            return protoInstrumentationScope;
        } else {
            return protoInstrumentationScopeAdapter.getUpdated();
        }
    }


}

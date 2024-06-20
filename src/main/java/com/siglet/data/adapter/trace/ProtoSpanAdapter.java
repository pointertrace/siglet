package com.siglet.data.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.data.Clonable;
import com.siglet.data.adapter.*;
import com.siglet.data.modifiable.trace.ModifiableSpan;
import com.siglet.data.trace.SpanKind;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ProtoSpanAdapter implements ModifiableSpan, Clonable {

    private final Span protoSpan;

    private final Resource protoResource;

    private final InstrumentationScope protoInstrumentationScope;

    private final boolean updatable;

    private boolean updated;

    private Span.Builder protoSpanBuilder;

    private ProtoAttributesAdapter protoAttributesAdapter;

    private ProtoResourceAdapter protoResourceAdapter;

    private ProtoLinksAdapter protoLinksAdapter;

    private ProtoEventsAdapter protoEventsAdapter;

    private ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter;


    public ProtoSpanAdapter(Span protoSpan, Resource protoResource, InstrumentationScope protoInstrumentationScope,
                            boolean updatable) {
        this.protoSpan = protoSpan;
        this.protoResource = protoResource;
        this.protoInstrumentationScope = protoInstrumentationScope;
        this.updatable = updatable;
    }

    @Override
    public long getTraceIdHigh() {
        return ByteBuffer.wrap(
                        Arrays.copyOfRange(protoSpanBuilder == null ?
                                protoSpan.getTraceId().toByteArray() :
                                protoSpanBuilder.getTraceId().toByteArray(), 0, 8))
                .getLong();
    }

    @Override
    public long getTraceIdLow() {
        return ByteBuffer.wrap(
                        Arrays.copyOfRange(protoSpanBuilder == null ?
                                protoSpan.getTraceId().toByteArray() :
                                protoSpanBuilder.getTraceId().toByteArray(), 8, 16))
                .getLong();
    }

    @Override
    public byte[] getTraceId() {
        return protoSpanBuilder == null ?
                protoSpan.getTraceId().toByteArray() : protoSpanBuilder.getTraceId().toByteArray();
    }

    @Override
    public void setTraceId(long high, long low) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setTraceId(ByteString.copyFrom(
                ByteBuffer.allocate(Long.BYTES * 2).putLong(high).putLong(low).array()));
    }

    @Override
    public void setTraceId(byte[] traceId) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setTraceId(ByteString.copyFrom(traceId));
    }

    @Override
    public long getSpanId() {
        return ByteBuffer.wrap(
                        protoSpanBuilder == null ?
                                protoSpan.getSpanId().toByteArray() :
                                protoSpanBuilder.getSpanId().toByteArray())
                .getLong();
    }

    @Override
    public void setSpanId(long spanId) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setSpanId(ByteString.copyFrom(ByteBuffer.allocate(Long.BYTES).putLong(spanId).array()));
    }

    @Override
    public long getParentSpanId() {
        return ByteBuffer.wrap(
                        protoSpanBuilder == null ?
                                protoSpan.getParentSpanId().toByteArray() :
                                protoSpanBuilder.getParentSpanId().toByteArray())
                .getLong();
    }

    @Override
    public void setParentSpanId(long parentSpanId) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setParentSpanId(ByteString.copyFrom(
                ByteBuffer.allocate(Long.BYTES).putLong(parentSpanId).array()));
    }

    @Override
    public String getTraceState() {
        return protoSpanBuilder == null ? protoSpan.getTraceState() : protoSpanBuilder.getTraceState();
    }

    @Override
    public void setTraceState(String traceState) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setTraceState(traceState);
    }

    @Override
    public String getName() {
        return protoSpanBuilder == null ? protoSpan.getName() : protoSpanBuilder.getName();
    }

    @Override
    public void setName(String name) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setName(name);
    }

    @Override
    public long getStartUnixNano() {
        return protoSpanBuilder == null ? protoSpan.getStartTimeUnixNano() : protoSpanBuilder.getStartTimeUnixNano();
    }

    @Override
    public long getEndUnixNano() {
        return protoSpanBuilder == null ? protoSpan.getEndTimeUnixNano() : protoSpanBuilder.getEndTimeUnixNano();
    }

    @Override
    public SpanKind getKind() {
        return AdapterUtils.getKind(protoSpanBuilder == null ? protoSpan.getKind() : protoSpanBuilder.getKind());
    }

    @Override
    public void setKind(SpanKind spanKind) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setKind(AdapterUtils.getKind(spanKind));
    }

    @Override
    public ProtoAttributesAdapter getAttributes() {
        if (protoAttributesAdapter == null) {
            protoAttributesAdapter = new ProtoAttributesAdapter(protoSpan.getAttributesList(), updatable);
        }
        return protoAttributesAdapter;
    }

    @Override
    public ProtoResourceAdapter getResource() {
        if (protoResourceAdapter == null) {
            protoResourceAdapter = new ProtoResourceAdapter(protoResource, updatable);
        }
        return protoResourceAdapter;
    }

    @Override
    public ProtoInstrumentationScopeAdapter getInstrumentationScope() {
        if (protoInstrumentationScopeAdapter == null) {
            protoInstrumentationScopeAdapter = new ProtoInstrumentationScopeAdapter(protoInstrumentationScope, updatable);
        }
        return protoInstrumentationScopeAdapter;
    }

    @Override
    public ProtoLinksAdapter getLinks() {
        if (protoLinksAdapter == null) {
            protoLinksAdapter = new ProtoLinksAdapter(protoSpan.getLinksList(), updatable);
        }
        return protoLinksAdapter;
    }

    @Override
    public ProtoEventsAdapter getEvents() {
        if (protoEventsAdapter == null) {
            protoEventsAdapter = new ProtoEventsAdapter(protoSpan.getEventsList(), updatable);
        }
        return protoEventsAdapter;
    }

    public Span getProtoSpan() {
        return protoSpan;
    }


    @Override
    public void setStartTimeUnixNano(long startTimeUnixNano) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setStartTimeUnixNano(startTimeUnixNano);
    }

    @Override
    public void setEndTimeUnixNano(long endTimeUnixNano) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setEndTimeUnixNano(endTimeUnixNano);
    }

    @Override
    public int getFlags() {
        return protoSpanBuilder == null ? protoSpan.getFlags() : protoSpanBuilder.getFlags();
    }

    @Override
    public void setFlags(int flags) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setFlags(flags);
    }

    @Override
    public int getDroppedAttributesCount() {
        return protoSpanBuilder == null ? protoSpan.getDroppedAttributesCount() : protoSpanBuilder.getDroppedAttributesCount();
    }

    @Override
    public void setDroppedAttributesCount(int droppedAttributesCount) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setDroppedAttributesCount(droppedAttributesCount);
    }

    @Override
    public int getDroppedEventsCount() {
        return protoSpanBuilder == null ? protoSpan.getDroppedEventsCount() : protoSpanBuilder.getDroppedEventsCount();
    }

    @Override
    public void setDroppedEventsCount(int droppedEventsCount) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setDroppedEventsCount(droppedEventsCount);
    }

    @Override
    public int getDroppedLinksCount() {
        return protoSpanBuilder == null ? protoSpan.getDroppedLinksCount() : protoSpanBuilder.getDroppedLinksCount();
    }

    @Override
    public void setDroppedLinksCount(int droppedLinksCount) {
        checkAndPrepareUpdate();
        protoSpanBuilder.setDroppedLinksCount(droppedLinksCount);
    }

    public boolean isUpdated() {
        return updated;
    }


    ProtoSpanAdapter getUpdated(boolean updatable) {
        if (!updatable) {
            return this;
        } else if (!updated && !attributesUpdated() && !resourceUpdated() && linksUpdated() && eventsUpdated() &&
                instrumentationScopeUpdated()) {
            return this;
        } else {
            Span.Builder spanbuilder = null;
            Resource resource = null;
            InstrumentationScope insScope = null;
            if (updated) {
                spanbuilder = protoSpanBuilder;
            } else {
                spanbuilder = protoSpan.toBuilder();
            }
            if (attributesUpdated()) {
                spanbuilder.clearAttributes();
                spanbuilder.addAllAttributes(protoAttributesAdapter.getUpdated());
            }
            if (linksUpdated()) {
                spanbuilder.clearLinks();
                spanbuilder.addAllLinks(protoLinksAdapter.getUpdated());
            }
            return new ProtoSpanAdapter(spanbuilder.build(), resource, insScope, updatable);
        }
    }

    @Override
    public ProtoSpanAdapter clone() {
        return new ProtoSpanAdapter(getUpdatedSpan(),
                getUpdatedResource(), getUpdatedInstrumentationScope(), updatable);
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

    public Span getUpdatedSpan() {
        if (!updatable) {
            return protoSpan;
        } else if (!updated && !attributesUpdated() && !linksUpdated() && !eventsUpdated()) {
            return protoSpan;
        } else {
            Span.Builder bld = protoSpanBuilder != null ? protoSpanBuilder : protoSpan.toBuilder();
            if (protoAttributesAdapter != null && protoAttributesAdapter.isUpdated()) {
                bld.clearAttributes();
                bld.addAllAttributes(protoAttributesAdapter.getAsKeyValueList());
            }
            if (protoLinksAdapter != null && protoLinksAdapter.isUpdated()) {
                bld.clearLinks();
                bld.addAllLinks(protoLinksAdapter.getUpdated());
            }
            if (protoEventsAdapter != null && protoEventsAdapter.isUpdated()) {
                bld.clearEvents();
                bld.addAllEvents(protoEventsAdapter.getUpdated());
            }
            return bld.build();

        }
    }

    public Resource getUpdatedResource() {
        if (!updatable) {
            return protoResource;
        } else if (protoResourceAdapter == null || !protoResourceAdapter.isUpdated()) {
            return protoResource;
        } else {
            return protoResourceAdapter.getUpdated();
        }
    }

    public InstrumentationScope getUpdatedInstrumentationScope() {
        if (!updatable) {
            return protoInstrumentationScope;
        } else if (protoInstrumentationScopeAdapter == null || !protoInstrumentationScopeAdapter.isUpdated()) {
            return protoInstrumentationScope;
        } else {
            return protoInstrumentationScopeAdapter.getUpdated();
        }
    }

    private void checkAndPrepareUpdate() {
        if (!updatable) {
            throw new IllegalStateException("trying to change a non updatable span");
        }
        if (protoSpanBuilder == null) {
            protoSpanBuilder = protoSpan.toBuilder();
        }
        updated = true;
    }


}

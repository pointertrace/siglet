package io.github.pointertrace.siglet.impl.adapter.trace;

import com.google.protobuf.ByteString;
import io.github.pointertrace.siglet.api.signal.trace.Span;
import io.github.pointertrace.siglet.api.signal.trace.SpanKind;
import io.github.pointertrace.siglet.impl.adapter.*;

import java.util.Arrays;
import java.util.Objects;


public final class SpanAdapter implements Span, EnqueuedTimeObservable {

    private final io.opentelemetry.proto.trace.v1.Span original;
    private io.opentelemetry.proto.trace.v1.Span.Builder builder;
    private final io.opentelemetry.proto.resource.v1.Resource resource;
    private final io.opentelemetry.proto.common.v1.InstrumentationScope scope;
    private ResourceAdapter resourceAdapter;
    private InstrumentationScopeAdapter scopeAdapter;
    private StatusAdapter statusAdapter;
    private AttributesAdapter attributesAdapter;
    private LinksAdapter linksAdapter;
    private EventsAdapter eventsAdapter;

    private long enqueuedTimeNanos;

    public SpanAdapter(io.opentelemetry.proto.trace.v1.Span span,
                       io.opentelemetry.proto.resource.v1.Resource resource,
                       io.opentelemetry.proto.common.v1.InstrumentationScope scope) {
        this.original = Objects.requireNonNull(span, "span");
        this.resource = Objects.requireNonNull(resource, "resource");
        this.scope = Objects.requireNonNull(scope, "scope");
    }

    private io.opentelemetry.proto.trace.v1.Span.Builder mutate() {
        if (builder == null) {
            builder = original.toBuilder();
        }
        return builder;
    }

    private io.opentelemetry.proto.trace.v1.SpanOrBuilder view() {
        return builder == null ? original : builder;
    }

    public io.opentelemetry.proto.trace.v1.Span getUpdated() {
        return builder == null ? original : builder.build();
    }

    public io.opentelemetry.proto.resource.v1.Resource getUpdatedResource() {
        return getResourceAdapter().getUpdated();
    }

    public io.opentelemetry.proto.common.v1.InstrumentationScope getUpdatedScope() {
        return getScopeAdapter().getUpdated();
    }

    private ResourceAdapter getResourceAdapter() {
        if (resourceAdapter == null) {
            resourceAdapter = new ResourceAdapter(resource);
        }
        return resourceAdapter;
    }

    private InstrumentationScopeAdapter getScopeAdapter() {
        if (scopeAdapter == null) {
            scopeAdapter = new InstrumentationScopeAdapter(scope);
        }
        return scopeAdapter;
    }

    public SpanAdapter duplicate() {
        return new SpanAdapter(getUpdated(), getUpdatedResource(), getUpdatedScope());
    }

    @Override
    public String getId() {
        return getTraceIdEx() + "/" + getSpanIdEx();
    }

    @Override
    public boolean isRoot() {
        return Arrays.equals(view().getParentSpanId().toByteArray(), new byte[8]);
    }

    @Override
    public String getTraceIdEx() {
        return ProtoUtil.hex(view().getTraceId().toByteArray());
    }

    @Override
    public long getTraceIdHigh() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 0);
    }

    @Override
    public long getTraceIdLow() {
        return ProtoUtil.readLong(view().getTraceId().toByteArray(), 8);
    }

    @Override
    public byte[] getTraceId() {
        return view().getTraceId().toByteArray();
    }

    @Override
    public SpanAdapter setTraceId(long high, long low) {
        mutate().setTraceId(ByteString.copyFrom(ProtoUtil.toTraceId(high, low)));
        return this;
    }

    @Override
    public SpanAdapter setTraceId(byte[] traceId) {
        mutate().setTraceId(ByteString.copyFrom(Objects.requireNonNull(traceId, "traceId")));
        return this;
    }

    @Override
    public String getSpanIdEx() {
        return ProtoUtil.hex(view().getSpanId().toByteArray());
    }

    @Override
    public long getSpanId() {
        return ProtoUtil.readLong(view().getSpanId().toByteArray(), 0);
    }

    @Override
    public SpanAdapter setSpanId(long spanId) {
        mutate().setSpanId(ByteString.copyFrom(ProtoUtil.toSpanId(spanId)));
        return this;
    }

    @Override
    public String getParentSpanIdEx() {
        return ProtoUtil.hex(view().getParentSpanId().toByteArray());
    }

    @Override
    public long getParentSpanId() {
        return ProtoUtil.readLong(view().getParentSpanId().toByteArray(), 0);
    }

    @Override
    public SpanAdapter setParentSpanId(long parentSpanId) {
        mutate().setParentSpanId(ByteString.copyFrom(ProtoUtil.toSpanId(parentSpanId)));
        return this;
    }

    @Override
    public String getTraceState() {
        return view().getTraceState();
    }

    @Override
    public SpanAdapter setTraceState(String traceState) {
        mutate().setTraceState(Objects.requireNonNull(traceState, "traceState"));
        return this;
    }

    @Override
    public String getName() {
        return view().getName();
    }

    @Override
    public SpanAdapter setName(String name) {
        mutate().setName(Objects.requireNonNull(name, "name"));
        return this;
    }

    @Override
    public long getStartTimeUnixNano() {
        return view().getStartTimeUnixNano();
    }

    @Override
    public SpanAdapter setStartTimeUnixNano(long startTimeUnixNano) {
        mutate().setStartTimeUnixNano(startTimeUnixNano);
        return this;
    }

    @Override
    public long getEndTimeUnixNano() {
        return view().getEndTimeUnixNano();
    }

    @Override
    public SpanAdapter setEndTimeUnixNano(long endTimeUnixNano) {
        mutate().setEndTimeUnixNano(endTimeUnixNano);
        return this;
    }

    @Override
    public SpanKind getKind() {
        return ProtoUtil.fromProto(view().getKind());
    }

    @Override
    public SpanAdapter setKind(SpanKind kind) {
        mutate().setKind(ProtoUtil.toProto(Objects.requireNonNull(kind, "kind")));
        return this;
    }

    @Override
    public StatusAdapter getStatus() {
        if (statusAdapter == null) {
            io.opentelemetry.proto.trace.v1.Status status = view().hasStatus()
                ? view().getStatus()
                : io.opentelemetry.proto.trace.v1.Status.getDefaultInstance();
            statusAdapter = new StatusAdapter(status, updated -> mutate().setStatus(updated));
        }
        return statusAdapter;
    }

    @Override
    public AttributesAdapter getAttributes() {
        if (attributesAdapter == null) {
            attributesAdapter = new AttributesAdapter(view().getAttributesList(), values -> {
                io.opentelemetry.proto.trace.v1.Span.Builder b = mutate();
                b.clearAttributes();
                b.addAllAttributes(values);
            });
        }
        return attributesAdapter;
    }

    @Override
    public LinksAdapter getLinks() {
        if (linksAdapter == null) {
            linksAdapter = new LinksAdapter(view().getLinksList(), values -> {
                io.opentelemetry.proto.trace.v1.Span.Builder b = mutate();
                b.clearLinks();
                b.addAllLinks(values);
            });
        }
        return linksAdapter;
    }

    @Override
    public EventsAdapter getEvents() {
        if (eventsAdapter == null) {
            eventsAdapter = new EventsAdapter(view().getEventsList(), values -> {
                io.opentelemetry.proto.trace.v1.Span.Builder b = mutate();
                b.clearEvents();
                b.addAllEvents(values);
            });
        }
        return eventsAdapter;
    }

    @Override
    public ResourceAdapter getResource() {
        return getResourceAdapter();
    }

    @Override
    public InstrumentationScopeAdapter getInstrumentationScope() {
        return getScopeAdapter();
    }

    @Override
    public int getFlags() {
        return view().getFlags();
    }

    @Override
    public SpanAdapter setFlags(int flags) {
        mutate().setFlags(flags);
        return this;
    }

    @Override
    public int getDroppedAttributesCount() {
        return view().getDroppedAttributesCount();
    }

    @Override
    public SpanAdapter setDroppedAttributesCount(int droppedAttributesCount) {
        mutate().setDroppedAttributesCount(droppedAttributesCount);
        return this;
    }

    @Override
    public int getDroppedEventsCount() {
        return view().getDroppedEventsCount();
    }

    @Override
    public SpanAdapter setDroppedEventsCount(int droppedEventsCount) {
        mutate().setDroppedEventsCount(droppedEventsCount);
        return this;
    }

    @Override
    public int getDroppedLinksCount() {
        return view().getDroppedLinksCount();
    }

    @Override
    public SpanAdapter setDroppedLinksCount(int droppedLinksCount) {
        mutate().setDroppedLinksCount(droppedLinksCount);
        return this;
    }

    @Override
    public void markEnqueued() {
        enqueuedTimeNanos = System.nanoTime();
    }

    @Override
    public long getQueuedTimeNanos() {
        return Math.max(0, System.nanoTime() - enqueuedTimeNanos);
    }
}

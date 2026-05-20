package io.github.pointertrace.siglet.impl.adapter.trace;

import io.github.pointertrace.siglet.api.signal.trace.SpanKind;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;

class SpanAdapterTest {

    @Test
    void shouldKeepSameProtoInstanceWhenUnchanged() {

        Span span = Span.newBuilder().setName("original").build();
        Resource resource = Resource.getDefaultInstance();
        InstrumentationScope scope = InstrumentationScope.getDefaultInstance();

        SpanAdapter adapter = new SpanAdapter(span, resource, scope);

        assertSame(span, adapter.getUpdated());
        assertSame(resource, adapter.getUpdatedResource());
        assertSame(scope, adapter.getUpdatedScope());
    }

    @Test
    void shouldReturnSameContainedAdapterInstances() {
        Status status = Status.newBuilder().setCode(Status.StatusCode.STATUS_CODE_OK).build();
        Span.Link link = Span.Link.newBuilder().setTraceState("state").build();
        Span.Event event = Span.Event.newBuilder().setName("evt").build();
        Span span = Span.newBuilder()
                .setName("original")
                .addAllAttributes(List.of(KeyValue.newBuilder().setKey("key").setValue(AnyValue.newBuilder().setStringValue("value").build()).build()))
                .setStatus(status)
                .addLinks(link)
                .addEvents(event)
                .build();
        List<KeyValue> attributes = span.getAttributesList();
        List<Span.Link> links = span.getLinksList();
        List<Span.Event> events = span.getEventsList();
        Resource resource = Resource.getDefaultInstance();
        InstrumentationScope scope = InstrumentationScope.getDefaultInstance();

        SpanAdapter adapter = new SpanAdapter(span, resource, scope);

        assertSame(status, adapter.getUpdated().getStatus());
        assertSame(attributes, adapter.getUpdated().getAttributesList());
        assertSame(links, adapter.getUpdated().getLinksList());
        assertSame(events, adapter.getUpdated().getEventsList());
        assertSame(resource, adapter.getUpdatedResource());
        assertSame(scope, adapter.getUpdatedScope());
    }

    @Test
    void shouldMutateThroughBuilderAfterStateChange() {
        Span span = Span.getDefaultInstance();
        SpanAdapter adapter = new SpanAdapter(
            span,
            Resource.getDefaultInstance(),
            InstrumentationScope.getDefaultInstance());

        adapter.setName("span-1").setKind(SpanKind.CLIENT).setFlags(3);
        Span updated = adapter.getUpdated();

        assertNotSame(span, updated);
        assertEquals("span-1", updated.getName());
        assertEquals(Span.SpanKind.SPAN_KIND_CLIENT, updated.getKind());
        assertEquals(3, updated.getFlags());
    }

    @Test
    void shouldRejectNullSpan() {
        assertThrows(NullPointerException.class, () -> new SpanAdapter(
            null,
            Resource.getDefaultInstance(),
            InstrumentationScope.getDefaultInstance()));
    }

    @Test
    void shouldBeLazy() {
        Span span = Span.newBuilder().setName("s1").build();
        Resource resource = Resource.getDefaultInstance();
        InstrumentationScope scope = InstrumentationScope.getDefaultInstance();

        SpanAdapter adapter = new SpanAdapter(span, resource, scope);

        // Accessing fields via joor to verify they are null
        try {
            assertNull(on(adapter).field("resourceAdapter").get(), "ResourceAdapter should be null initially");
            assertNull(on(adapter).field("scopeAdapter").get(), "InstrumentationScopeAdapter should be null initially");
            assertNull(on(adapter).field("attributesAdapter").get(), "AttributesAdapter should be null initially");

            adapter.getResource();
            assertNotNull(on(adapter).field("resourceAdapter").get(), "ResourceAdapter should be initialized after getResource()");

            adapter.getInstrumentationScope();
            assertNotNull(on(adapter).field("scopeAdapter").get(), "InstrumentationScopeAdapter should be initialized after getInstrumentationScope()");

            adapter.getAttributes();
            assertNotNull(on(adapter).field("attributesAdapter").get(), "AttributesAdapter should be initialized after getAttributes()");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

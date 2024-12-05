package com.siglet.data.adapter.trace;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.*;
import com.siglet.data.trace.SpanKind;
import com.siglet.data.trace.StatusCode;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoSpanAdapterAdapterTest {

    private Span protoSpan;

    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoSpanAdapter protoSpanAdapter;

    @BeforeEach
    void setUp() {
        protoSpan = Span.newBuilder()
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0, 2)))
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setParentSpanId(ByteString.copyFrom(AdapterUtils.spanId(3)))
                .setName("span-name")
                .setStartTimeUnixNano(1L)
                .setEndTimeUnixNano(2L)
                .setFlags(1)
                .setTraceState("trace-state")
                .setDroppedAttributesCount(1)
                .setDroppedEventsCount(2)
                .setDroppedLinksCount(3)
                .setKind(Span.SpanKind.SPAN_KIND_CLIENT)
                .setStatus(Status.newBuilder()
                        .setCode(Status.StatusCode.STATUS_CODE_OK)
                        .setMessage("status-message")
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("str-attribute-value").build())
                        .build())
                .addAttributes(KeyValue.newBuilder()
                        .setKey("long-attribute")
                        .setValue(AnyValue.newBuilder().setIntValue(10L).build())
                        .build())
                .addLinks(Span.Link.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(4)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0L, 5L)))
                        .setTraceState("first-link-trace-state")
                        .setFlags(2)
                        .setDroppedAttributesCount(10)
                        .addAttributes(KeyValue.newBuilder()
                                .setKey("lnk-str-attribute")
                                .setValue(AnyValue.newBuilder().setStringValue("lnk-str-attribute-value").build())
                                .build())
                        .build())
                .addLinks(Span.Link.newBuilder()
                        .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(6)))
                        .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(0L, 7L)))
                        .setTraceState("second-link-trace-state")
                        .setFlags(3)
                        .setDroppedAttributesCount(20)
                        .build())
                .addEvents(Span.Event.newBuilder()
                        .setName("first-event-name")
                        .setTimeUnixNano(1)
                        .setDroppedAttributesCount(30)
                        .addAttributes(KeyValue.newBuilder()
                                .setKey("evt-str-attribute")
                                .setValue(AnyValue.newBuilder().setStringValue("evt-str-attribute-value").build())
                                .build())
                        .build())
                .addEvents(Span.Event.newBuilder()
                        .setName("second-event-name")
                        .setTimeUnixNano(2)
                        .setDroppedAttributesCount(40)
                        .build())
                .build();

        protoResource = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("rs-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("rs-str-attribute-value").build())
                        .build())
                .build();

        protoInstrumentationScope = InstrumentationScope.newBuilder()
                .setName("instrumentation-scope-name")
                .setVersion("instrumentation-scope-version")
                .setDroppedAttributesCount(3)
                .addAttributes(KeyValue.newBuilder()
                        .setKey("is-str-attribute")
                        .setValue(AnyValue.newBuilder().setStringValue("is-str-attribute-value").build())
                        .build())
                .build();

        protoSpanAdapter = new ProtoSpanAdapter(protoSpan, protoResource, protoInstrumentationScope, true);

    }

    @Test
    public void setAndGet() {
        protoSpanAdapter
                .setTraceId(10L, 20L)
                .setSpanId(10L)
                .setParentSpanId(30L)
                .setName("new-name")
                .setStartTimeUnixNano(3L)
                .setEndTimeUnixNano(4L)
                .setFlags(2)
                .setTraceState("new-trace-state")
                .setDroppedAttributesCount(10)
                .setDroppedEventsCount(20)
                .setDroppedLinksCount(30)
                .setKind(SpanKind.SERVER);

        assertEquals(10, protoSpanAdapter.getTraceIdHigh());
        assertEquals(20, protoSpanAdapter.getTraceIdLow());
        assertEquals(10L, protoSpanAdapter.getSpanId());
        assertEquals(30L, protoSpanAdapter.getParentSpanId());
        assertEquals("new-name", protoSpanAdapter.getName());
        assertEquals(3L, protoSpanAdapter.getStartTimeUnixNano());
        assertEquals(4L, protoSpanAdapter.getEndTimeUnixNano());
        assertEquals(2, protoSpanAdapter.getFlags());
        assertEquals("new-trace-state", protoSpanAdapter.getTraceState());
        assertEquals(10, protoSpanAdapter.getDroppedAttributesCount());
        assertEquals(20, protoSpanAdapter.getDroppedEventsCount());
        assertEquals(30, protoSpanAdapter.getDroppedLinksCount());
        assertEquals(SpanKind.SERVER, protoSpanAdapter.getKind());
        assertFalse(protoSpanAdapter.isRoot());

    }

    @Test
    public void setNonUpdatable() {
        protoSpanAdapter = new ProtoSpanAdapter(Span.newBuilder().build(), Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), false);

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setTraceId(10L, 20L));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setSpanId(10L));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setParentSpanId(30L));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setName("new-name"));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setStartTimeUnixNano(3L));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setEndTimeUnixNano(4L));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setFlags(2));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setTraceState("new-trace-state"));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setDroppedAttributesCount(10));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setDroppedEventsCount(20));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setDroppedLinksCount(30));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.setKind(SpanKind.SERVER));

        assertThrowsExactly(SigletError.class, () ->
                protoSpanAdapter.getResource().setDroppedAttributesCount(1));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getStatus().setCode(StatusCode.OK));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getAttributes().remove("str-key"));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getLinks().remove(0, 0, 0));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getInstrumentationScope().setName("new-name"));

        assertFalse(protoSpanAdapter.isUpdated());
    }

    @Test
    public void setAndGetNotChangedValues() {
        protoSpanAdapter.setTraceId(10L, 20L);

        assertEquals(1L,protoSpanAdapter.getSpanId());
        assertEquals(3L, protoSpanAdapter.getParentSpanId());
        assertEquals("span-name",protoSpanAdapter.getName());
        assertEquals(1L, protoSpanAdapter.getStartTimeUnixNano());
        assertEquals(2L, protoSpanAdapter.getEndTimeUnixNano());
        assertEquals(1, protoSpanAdapter.getFlags());
        assertEquals("trace-state",protoSpanAdapter.getTraceState());
        assertEquals(1,protoSpanAdapter.getDroppedAttributesCount());
        assertEquals(2, protoSpanAdapter.getDroppedEventsCount());
        assertEquals(3, protoSpanAdapter.getDroppedLinksCount());
        assertEquals(SpanKind.CLIENT,protoSpanAdapter.getKind());
        assertFalse(protoSpanAdapter.isRoot());

    }

    @Test
    public void get() {

        assertEquals(0, protoSpanAdapter.getTraceIdHigh());
        assertEquals(2, protoSpanAdapter.getTraceIdLow());
        assertEquals(1L, protoSpanAdapter.getSpanId());
        assertEquals(3L, protoSpanAdapter.getParentSpanId());
        assertEquals("span-name",protoSpanAdapter.getName());
        assertEquals(1L, protoSpanAdapter.getStartTimeUnixNano());
        assertEquals(2L, protoSpanAdapter.getEndTimeUnixNano());
        assertEquals(1, protoSpanAdapter.getFlags());
        assertEquals("trace-state",protoSpanAdapter.getTraceState());
        assertEquals(1, protoSpanAdapter.getDroppedAttributesCount());
        assertEquals(2,protoSpanAdapter.getDroppedEventsCount());
        assertEquals(3,protoSpanAdapter.getDroppedLinksCount());
        assertEquals(SpanKind.CLIENT,protoSpanAdapter.getKind());
        assertFalse(protoSpanAdapter.isRoot());
    }

    @Test
    public void statusGet() {
        ProtoStatusAdapter protoStatusAdapter = protoSpanAdapter.getStatus();

        assertEquals(StatusCode.OK, protoStatusAdapter.getCode());
        assertEquals("status-message", protoStatusAdapter.getStatusMessage());

    }

    @Test
    public void statusGetAndSet() {
        ProtoStatusAdapter protoStatusAdapter = protoSpanAdapter.getStatus();

        protoStatusAdapter.setCode(StatusCode.ERROR);
        protoStatusAdapter.setStatusMessage("new-status-message");

        assertEquals(StatusCode.ERROR, protoStatusAdapter.getCode());
        assertEquals("new-status-message", protoStatusAdapter.getStatusMessage());

    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("str-attribute-value", protoAttributesAdapter.getAsString("str-attribute"));

        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.containsKey("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals("new-str-attribute-value",protoAttributesAdapter.getAsString("str-attribute"));

        assertTrue(protoAttributesAdapter.isUpdated());
    }

    @Test
    public void linksGet() {

        ProtoLinksAdapter protoLinksAdapter = protoSpanAdapter.getLinks();

        assertEquals(2, protoLinksAdapter.getSize());
        assertTrue(protoLinksAdapter.has(0, 5, 4));

        ProtoLinkAdapter protoLinkAdapter = protoLinksAdapter.get(0, 5, 4);
        assertNotNull(protoLinkAdapter);
        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(5, protoLinkAdapter.getTraceIdLow());
        assertEquals(4, protoLinkAdapter.getSpanId());

        ProtoAttributesAdapter attributes = protoLinkAdapter.getAttributes();

        assertEquals(1, attributes.getSize());

        assertTrue(attributes.containsKey("lnk-str-attribute"));
        assertTrue(attributes.isString("lnk-str-attribute"));
        assertEquals("lnk-str-attribute-value", attributes.getAsString("lnk-str-attribute"));

        protoLinkAdapter = protoLinksAdapter.get(0, 7, 6);
        assertNotNull(protoLinkAdapter);
        assertEquals(0, protoLinkAdapter.getTraceIdHigh());
        assertEquals(7, protoLinkAdapter.getTraceIdLow());
        assertEquals(6, protoLinkAdapter.getSpanId());


    }

    @Test
    public void eventsGet() {

        ProtoEventsAdapter protoEventsAdapter = protoSpanAdapter.getEvents();

        assertEquals(2, protoEventsAdapter.getSize());

        ProtoEventAdapter protoEventAdapter = protoEventsAdapter.get(0);
        assertNotNull(protoEventAdapter);
        assertEquals("first-event-name", protoEventAdapter.getName());

        ProtoAttributesAdapter attributes = protoEventAdapter.getAttributes();

        assertEquals(1, attributes.getSize());

        assertTrue(attributes.containsKey("evt-str-attribute"));
        assertTrue(attributes.isString("evt-str-attribute"));
        assertEquals("evt-str-attribute-value", attributes.getAsString("evt-str-attribute"));

        protoEventAdapter = protoEventsAdapter.get(1);
        assertNotNull(protoEventAdapter);
        assertEquals("second-event-name", protoEventAdapter.getName());


    }

    @Test
    public void resourceGet() {

        ProtoResourceAdapter protoResourceAdapter = protoSpanAdapter.getResource();

        assertEquals(2, protoResourceAdapter.getDroppedAttributesCount());

        ProtoAttributesAdapter attributes = protoResourceAdapter.getAttributes();

        assertEquals(1, attributes.getSize());

        assertTrue(attributes.containsKey("rs-str-attribute"));
        assertTrue(attributes.isString("rs-str-attribute"));
        assertEquals("rs-str-attribute-value", attributes.getAsString("rs-str-attribute"));

    }


    @Test
    public void instrumentationScopeGet() {
        ProtoInstrumentationScopeAdapter protoInstrumentationScopeAdapter =
                protoSpanAdapter.getInstrumentationScope();

        assertEquals("instrumentation-scope-name", protoInstrumentationScopeAdapter.getName());
        assertEquals("instrumentation-scope-version", protoInstrumentationScopeAdapter.getVersion());

        ProtoAttributesAdapter attributes = protoInstrumentationScopeAdapter.getAttributes();

        assertEquals(1, attributes.getSize());

        assertTrue(attributes.containsKey("is-str-attribute"));
        assertTrue(attributes.isString("is-str-attribute"));
        assertEquals("is-str-attribute-value", attributes.getAsString("is-str-attribute"));
    }

    @Test
    public void getUpdatedSpan_notUpdatable() {

        protoSpanAdapter = new ProtoSpanAdapter(protoSpan, protoResource, protoInstrumentationScope, false);
        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedSpan_nothingUpdated() {

        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedSpan_onlySpanUpdated() {

        protoSpanAdapter
                .setTraceId(10, 20)
                .setSpanId(30)
                .setParentSpanId(40)
                .setName("new-name")
                .setStartTimeUnixNano(3)
                .setEndTimeUnixNano(4)
                .setFlags(2)
                .setTraceState("new-trace-state")
                .setDroppedAttributesCount(10)
                .setDroppedEventsCount(20)
                .setDroppedLinksCount(30)
                .setKind(SpanKind.SERVER);


        Span actual = protoSpanAdapter.getUpdated();
        assertEquals(10, AdapterUtils.traceIdHigh(actual.getTraceId().toByteArray()));
        assertEquals(20, AdapterUtils.traceIdLow(actual.getTraceId().toByteArray()));
        assertEquals(30, AdapterUtils.spanId(actual.getSpanId().toByteArray()));
        assertEquals(40, AdapterUtils.spanId(actual.getParentSpanId().toByteArray()));
        assertEquals("new-name", actual.getName());
        assertEquals(3, actual.getStartTimeUnixNano());
        assertEquals(4, actual.getEndTimeUnixNano());
        assertEquals(2, actual.getFlags());
        assertEquals("new-trace-state", actual.getTraceState());
        assertEquals(10, actual.getDroppedAttributesCount());
        assertEquals(20, actual.getDroppedEventsCount());
        assertEquals(30, actual.getDroppedLinksCount());
        assertEquals(Span.SpanKind.SPAN_KIND_SERVER, actual.getKind());

        assertSame(protoSpan.getAttributesList(), actual.getAttributesList());
        assertSame(protoSpan.getLinksList(), actual.getLinksList());
        assertSame(protoSpan.getEventsList(), actual.getEventsList());

        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedSpan_onlyAttributesChange() {


        protoSpanAdapter.getAttributes().set("str-attribute", "new-value");

        Span actual = protoSpanAdapter.getUpdated();

        assertEquals(0, AdapterUtils.traceIdHigh(actual.getTraceId().toByteArray()));
        assertEquals(2, AdapterUtils.traceIdLow(actual.getTraceId().toByteArray()));
        assertEquals(1, AdapterUtils.spanId(actual.getSpanId().toByteArray()));
        assertEquals(3, AdapterUtils.spanId(actual.getParentSpanId().toByteArray()));
        assertEquals("span-name", actual.getName());
        assertEquals(1, actual.getStartTimeUnixNano());
        assertEquals(2, actual.getEndTimeUnixNano());
        assertEquals(1, actual.getFlags());
        assertEquals("trace-state", actual.getTraceState());
        assertEquals(1, actual.getDroppedAttributesCount());
        assertEquals(2, actual.getDroppedEventsCount());
        assertEquals(3, actual.getDroppedLinksCount());
        assertEquals(Span.SpanKind.SPAN_KIND_CLIENT, actual.getKind());

        assertNotSame(protoSpan.getAttributesList(), actual.getAttributesList());

        Map<String, Object> attributesMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());

        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals("new-value", attributesMap.get("str-attribute"));

        assertTrue(attributesMap.containsKey("long-attribute"));
        assertInstanceOf(Long.class, attributesMap.get("long-attribute"));
        assertEquals(10L, attributesMap.get("long-attribute"));

        assertSame(protoSpan.getLinksList(), actual.getLinksList());
        assertSame(protoSpan.getEventsList(), actual.getEventsList());

        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }


    @Test
    public void getUpdatedSpan_onlyEventsChange() {

        protoSpanAdapter.getEvents().remove(0);

        Span actual = protoSpanAdapter.getUpdated();

        assertEquals(0, AdapterUtils.traceIdHigh(actual.getTraceId().toByteArray()));
        assertEquals(2, AdapterUtils.traceIdLow(actual.getTraceId().toByteArray()));
        assertEquals(1, AdapterUtils.spanId(actual.getSpanId().toByteArray()));
        assertEquals(3, AdapterUtils.spanId(actual.getParentSpanId().toByteArray()));
        assertEquals("span-name", actual.getName());
        assertEquals(1, actual.getStartTimeUnixNano());
        assertEquals(2, actual.getEndTimeUnixNano());
        assertEquals(1, actual.getFlags());
        assertEquals("trace-state", actual.getTraceState());
        assertEquals(1, actual.getDroppedAttributesCount());
        assertEquals(2, actual.getDroppedEventsCount());
        assertEquals(3, actual.getDroppedLinksCount());
        assertEquals(Span.SpanKind.SPAN_KIND_CLIENT, actual.getKind());


        assertEquals(1, actual.getEventsList().size());
        Span.Event event = actual.getEventsList().getFirst();
        assertEquals("second-event-name", event.getName());
        assertEquals(2, event.getTimeUnixNano());
        assertEquals(40, event.getDroppedAttributesCount());


        assertSame(protoSpan.getAttributesList(), actual.getAttributesList());
        assertSame(protoSpan.getLinksList(), actual.getLinksList());

        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedResource_onlyResourceChange() {

        protoSpanAdapter.getResource().setDroppedAttributesCount(20);


        Resource actual = protoSpanAdapter.getUpdatedResource();

        assertEquals(20, actual.getDroppedAttributesCount());

        assertSame(protoResource.getAttributesList(), actual.getAttributesList());

        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedInstrumentationScope_onlyInstrumentationScopedChange() {

        protoSpanAdapter.getInstrumentationScope().setName("new-name");

        InstrumentationScope actual = protoSpanAdapter.getInstrumentationScope().getUpdated();

        assertEquals("new-name", actual.getName());

        assertSame(protoInstrumentationScope.getAttributesList(), actual.getAttributesList());

        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());

    }

    @Test
    public void isRoot() {
        Span span = Span.newBuilder()
                .setName("root span")
                .build();

        protoSpanAdapter = new ProtoSpanAdapter(span, Resource.newBuilder().build(),
                InstrumentationScope.newBuilder().build(), true);

        assertTrue(protoSpanAdapter.isRoot());
    }

    @Test
    public void clone_noChange() {

        ProtoSpanAdapter clonedSpanAdapter = protoSpanAdapter.cloneAdapter();

        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

        assertSame(protoSpan, clonedSpanAdapter.getUpdated());
        assertSame(protoResource, clonedSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, clonedSpanAdapter.getUpdatedInstrumentationScope());
    }

    @Test
    public void clone_changeOriginal() {

        ProtoSpanAdapter clonedSpanAdapter = protoSpanAdapter.cloneAdapter();

        protoSpanAdapter.setName("new-name");
        protoSpanAdapter.getResource().setDroppedAttributesCount(1000);
        protoSpanAdapter.getInstrumentationScope().setName("new-name");

        assertNotSame(protoSpan, protoSpanAdapter.getUpdated());
        assertEquals("new-name", protoSpanAdapter.getName());

        assertNotSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertEquals(1000,protoSpanAdapter.getUpdatedResource().getDroppedAttributesCount());

        assertNotSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());
        assertEquals("new-name", protoSpanAdapter.getInstrumentationScope().getName());

        assertSame(protoSpan, clonedSpanAdapter.getUpdated());
        assertSame(protoResource, clonedSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, clonedSpanAdapter.getUpdatedInstrumentationScope());
    }

    @Test
    public void clone_changeCloned() {

        ProtoSpanAdapter clonedSpanAdapter = protoSpanAdapter.cloneAdapter();

        clonedSpanAdapter.setName("new-name");
        clonedSpanAdapter.getResource().setDroppedAttributesCount(1000);
        clonedSpanAdapter.getInstrumentationScope().setName("new-name");

        assertNotSame(protoSpan, clonedSpanAdapter.getUpdated());
        assertEquals("new-name", clonedSpanAdapter.getName());

        assertNotSame(protoResource, clonedSpanAdapter.getUpdatedResource());
        assertEquals(1000,clonedSpanAdapter.getUpdatedResource().getDroppedAttributesCount());

        assertNotSame(protoInstrumentationScope, clonedSpanAdapter.getUpdatedInstrumentationScope());
        assertEquals("new-name", clonedSpanAdapter.getInstrumentationScope().getName());

        assertSame(protoSpan, protoSpanAdapter.getUpdated());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());
    }


    @Test
    public void clone_bothChanged() {

        ProtoSpanAdapter clonedSpanAdapter = protoSpanAdapter.cloneAdapter();

        protoSpanAdapter.setName("new-name-original");
        protoSpanAdapter.getResource().setDroppedAttributesCount(1000);
        protoSpanAdapter.getInstrumentationScope().setName("new-name-original");

        clonedSpanAdapter.setName("new-name-cloned");
        clonedSpanAdapter.getResource().setDroppedAttributesCount(2000);
        clonedSpanAdapter.getInstrumentationScope().setName("new-name-cloned");

        assertNotSame(protoSpan, protoSpanAdapter.getUpdated());
        assertEquals("new-name-original", protoSpanAdapter.getName());

        assertNotSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertEquals(1000,protoSpanAdapter.getUpdatedResource().getDroppedAttributesCount());

        assertNotSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());
        assertEquals("new-name-original", protoSpanAdapter.getInstrumentationScope().getName());


        assertNotSame(protoSpan, clonedSpanAdapter.getUpdated());
        assertEquals("new-name-cloned", clonedSpanAdapter.getName());

        assertNotSame(protoResource, clonedSpanAdapter.getUpdatedResource());
        assertEquals(2000,clonedSpanAdapter.getUpdatedResource().getDroppedAttributesCount());

        assertNotSame(protoInstrumentationScope, clonedSpanAdapter.getUpdatedInstrumentationScope());
        assertEquals("new-name-cloned", clonedSpanAdapter.getInstrumentationScope().getName());
    }
}
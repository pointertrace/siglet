package com.siglet.data.adapter.metric;

import com.google.protobuf.ByteString;
import com.siglet.SigletError;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.*;
import com.siglet.data.adapter.trace.ProtoLinkAdapter;
import com.siglet.data.adapter.trace.ProtoLinksAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.data.trace.SpanKind;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.*;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtoMetricAdapterTest {

    private Metric protoGaugeMetric;

    private Metric protoSumMetric;

    private Metric protoHistogramMetric;

    private Metric protoExponentialHistogramMetric;

    private Metric protoSummaryMetric;

    private Resource protoResource;

    private InstrumentationScope protoInstrumentationScope;

    private ProtoMetricAdapter protoGaugeMetricAdapter;

    private ProtoMetricAdapter protoSumMetricAdapter;

    private ProtoMetricAdapter protoHistogramMetricAdapter;

    private ProtoMetricAdapter protoExponentialHistogramMetricAdapter;

    private ProtoMetricAdapter protoSummaryMetricAdapter;

    @BeforeEach
    void setUp() {

        protoGaugeMetric = Metric.newBuilder()
                .setName("gauge-metric-name")
                .setDescription("gauge-metric description")
                .setUnit("gauge-metric-unit")
                .setGauge(Gauge.newBuilder()
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setTimeUnixNano(1)
                                .setStartTimeUnixNano(2)
                                .setAsInt(10)
                                .build())
                        .build())
                .build();

        protoSumMetric = Metric.newBuilder()
                .setName("sum-metric-name")
                .setDescription("sum-metric description")
                .setUnit("sum-metric-unit")
                .setSum(Sum.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_CUMULATIVE)
                        .setIsMonotonic(true)
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setTimeUnixNano(10)
                                .setStartTimeUnixNano(20)
                                .setAsDouble(10.10)
                                .build())
                        .build())
                .build();

        protoHistogramMetric = Metric.newBuilder()
                .setName("histogram-metric-name")
                .setDescription("histogram-metric description")
                .setUnit("histogram-metric-unit")
                .setHistogram(Histogram.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA)
                        .addDataPoints(HistogramDataPoint.newBuilder()
                                .setTimeUnixNano(100)
                                .setStartTimeUnixNano(200)
                                .setSum(100.111)
                                .build())
                        .build())
                .build();

        protoExponentialHistogramMetric = Metric.newBuilder()
                .setName("exponential-histogram-metric-name")
                .setDescription("exponential-histogram-metric description")
                .setUnit("exponential-histogram-metric-unit")
                .setExponentialHistogram(ExponentialHistogram.newBuilder()
                        .setAggregationTemporality(AggregationTemporality.AGGREGATION_TEMPORALITY_DELTA)
                        .addDataPoints(ExponentialHistogramDataPoint.newBuilder()
                                .setTimeUnixNano(1000)
                                .setStartTimeUnixNano(2000)
                                .setSum(1000.2222)
                                .build())
                        .build())
                .build();

        protoSummaryMetric = Metric.newBuilder()
                .setName("summary-histogram-metric-name")
                .setDescription("summary-histogram-metric description")
                .setUnit("summary-histogram-metric-unit")
                .setSummary(Summary.newBuilder()
                        .addDataPoints(SummaryDataPoint.newBuilder()
                                .setTimeUnixNano(1000)
                                .setStartTimeUnixNano(2000)
                                .setSum(1000.2222)
                                .build())
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

//        protoSpanAdapter = new ProtoSpanAdapter(protoMetric, protoResource, protoInstrumentationScope, true);

    }
/*
    @Test
    public void setAndGet() {
        protoSpanAdapter.setTraceId(10L, 20L);
        protoSpanAdapter.setSpanId(10L);
        protoSpanAdapter.setParentSpanId(30L);
        protoSpanAdapter.setName("new-name");
        protoSpanAdapter.setStartTimeUnixNano(3L);
        protoSpanAdapter.setEndTimeUnixNano(4L);
        protoSpanAdapter.setFlags(2);
        protoSpanAdapter.setTraceState("new-trace-state");
        protoSpanAdapter.setDroppedAttributesCount(10);
        protoSpanAdapter.setDroppedEventsCount(20);
        protoSpanAdapter.setDroppedLinksCount(30);
        protoSpanAdapter.setKind(SpanKind.SERVER);


        assertEquals(protoSpanAdapter.getTraceIdHigh(), 10);
        assertEquals(protoSpanAdapter.getTraceIdLow(), 20);
        assertEquals(protoSpanAdapter.getSpanId(), 10L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 30L);
        assertEquals(protoSpanAdapter.getName(), "new-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 3L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 4L);
        assertEquals(protoSpanAdapter.getFlags(), 2);
        assertEquals(protoSpanAdapter.getTraceState(), "new-trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 10);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 20);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 30);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.SERVER);

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

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getAttributes().remove("str-key"));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getLinks().remove(0, 0, 0));

        assertThrowsExactly(SigletError.class, () -> protoSpanAdapter.getInstrumentationScope().setName("new-name"));

        assertFalse(protoSpanAdapter.isUpdated());
    }

    @Test
    public void setAndGetNotChangedValues() {
        protoSpanAdapter.setTraceId(10L, 20L);

        assertEquals(protoSpanAdapter.getSpanId(), 1L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 3L);
        assertEquals(protoSpanAdapter.getName(), "span-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 1L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 2L);
        assertEquals(protoSpanAdapter.getFlags(), 1);
        assertEquals(protoSpanAdapter.getTraceState(), "trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 1);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 2);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 3);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.CLIENT);

    }

    @Test
    public void get() {

        assertEquals(protoSpanAdapter.getTraceIdHigh(), 0);
        assertEquals(protoSpanAdapter.getTraceIdLow(), 2);
        assertEquals(protoSpanAdapter.getSpanId(), 1L);
        assertEquals(protoSpanAdapter.getParentSpanId(), 3L);
        assertEquals(protoSpanAdapter.getName(), "span-name");
        assertEquals(protoSpanAdapter.getStartUnixNano(), 1L);
        assertEquals(protoSpanAdapter.getEndUnixNano(), 2L);
        assertEquals(protoSpanAdapter.getFlags(), 1);
        assertEquals(protoSpanAdapter.getTraceState(), "trace-state");
        assertEquals(protoSpanAdapter.getDroppedAttributesCount(), 1);
        assertEquals(protoSpanAdapter.getDroppedEventsCount(), 2);
        assertEquals(protoSpanAdapter.getDroppedLinksCount(), 3);
        assertEquals(protoSpanAdapter.getKind(), SpanKind.CLIENT);

    }

    @Test
    public void attributesGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "str-attribute-value");

        assertEquals(10L, protoAttributesAdapter.getAsLong("long-attribute"));

        assertFalse(protoAttributesAdapter.isUpdated());
    }


    @Test
    public void attributesChangeAndGet() {

        ProtoAttributesAdapter protoAttributesAdapter = protoSpanAdapter.getAttributes();

        protoAttributesAdapter.set("str-attribute", "new-str-attribute-value");
        protoAttributesAdapter.set("bool-attribute", true);
        protoAttributesAdapter.remove("long-attribute");

        assertTrue(protoAttributesAdapter.has("str-attribute"));
        assertTrue(protoAttributesAdapter.isString("str-attribute"));
        assertEquals(protoAttributesAdapter.getAsString("str-attribute"), "new-str-attribute-value");

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

        assertTrue(attributes.has("lnk-str-attribute"));
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

        assertTrue(attributes.has("evt-str-attribute"));
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

        assertTrue(attributes.has("rs-str-attribute"));
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

        assertTrue(attributes.has("is-str-attribute"));
        assertTrue(attributes.isString("is-str-attribute"));
        assertEquals("is-str-attribute-value", attributes.getAsString("is-str-attribute"));
    }

    @Test
    public void getUpdatedSpan_notUpdatable() {

        protoSpanAdapter = new ProtoSpanAdapter(protoMetric, protoResource, protoInstrumentationScope, false);
        assertSame(protoMetric, protoSpanAdapter.getUpdatedSpan());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedSpan_nothingUpdated() {

        assertSame(protoMetric, protoSpanAdapter.getUpdatedSpan());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedSpan_onlySpanUpdated() {

        protoSpanAdapter.setTraceId(10, 20);
        protoSpanAdapter.setSpanId(30);
        protoSpanAdapter.setParentSpanId(40);
        protoSpanAdapter.setName("new-name");
        protoSpanAdapter.setStartTimeUnixNano(3);
        protoSpanAdapter.setEndTimeUnixNano(4);
        protoSpanAdapter.setFlags(2);
        protoSpanAdapter.setTraceState("new-trace-state");
        protoSpanAdapter.setDroppedAttributesCount(10);
        protoSpanAdapter.setDroppedEventsCount(20);
        protoSpanAdapter.setDroppedLinksCount(30);
        protoSpanAdapter.setKind(SpanKind.SERVER);


        Span actual = protoSpanAdapter.getUpdatedSpan();
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
        assertEquals(Span.SpanKind.SPAN_KIND_SERVER,actual.getKind());

        assertSame(protoMetric.getAttributesList(), actual.getAttributesList());
        assertSame(protoMetric.getLinksList(), actual.getLinksList());
        assertSame(protoMetric.getEventsList(), actual.getEventsList());

        assertSame(protoResource,protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }
    @Test
    public void getUpdatedSpan_onlyAttributesChange() {


        protoSpanAdapter.getAttributes().set("str-attribute", "new-value");

        Span actual = protoSpanAdapter.getUpdatedSpan();

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
        assertEquals(Span.SpanKind.SPAN_KIND_CLIENT,actual.getKind());

        assertNotSame(protoMetric.getAttributesList(), actual.getAttributesList());

        Map<String, Object> attributesMap = AdapterUtils.keyValueListToMap(actual.getAttributesList());

        assertEquals(2, attributesMap.size());

        assertTrue(attributesMap.containsKey("str-attribute"));
        assertInstanceOf(String.class, attributesMap.get("str-attribute"));
        assertEquals("new-value", attributesMap.get("str-attribute"));

        assertTrue(attributesMap.containsKey("long-attribute"));
        assertInstanceOf(Long.class, attributesMap.get("long-attribute"));
        assertEquals(10L, attributesMap.get("long-attribute"));

        assertSame(protoMetric.getLinksList(), actual.getLinksList());
        assertSame(protoMetric.getEventsList(), actual.getEventsList());

        assertSame(protoResource,protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }


    @Test
    public void getUpdatedSpan_onlyEventsChange() {

        protoSpanAdapter.getEvents().remove(0);

        Span actual = protoSpanAdapter.getUpdatedSpan();

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
        assertEquals(Span.SpanKind.SPAN_KIND_CLIENT,actual.getKind());


        assertEquals(1, actual.getEventsList().size());
        Span.Event event = actual.getEventsList().get(0);
        assertEquals("second-event-name", event.getName());
        assertEquals(2, event.getTimeUnixNano());
        assertEquals(40, event.getDroppedAttributesCount());


        assertSame(protoMetric.getAttributesList(), actual.getAttributesList());
        assertSame(protoMetric.getLinksList(), actual.getLinksList());

        assertSame(protoResource,protoSpanAdapter.getUpdatedResource());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedResource_onlyResourceChange() {

        protoSpanAdapter.getResource().setDroppedAttributesCount(20);


        Resource actual = protoSpanAdapter.getUpdatedResource();

        assertEquals(20, actual.getDroppedAttributesCount());

        assertSame(protoResource.getAttributesList(), actual.getAttributesList());

        assertSame(protoMetric, protoSpanAdapter.getUpdatedSpan());
        assertSame(protoInstrumentationScope, protoSpanAdapter.getUpdatedInstrumentationScope());

    }

    @Test
    public void getUpdatedInstrumentationScope_onlyInstrumentationScopedChange() {

        protoSpanAdapter.getInstrumentationScope().setName("new-name");

        InstrumentationScope actual = protoSpanAdapter.getInstrumentationScope().getUpdated();

        assertEquals("new-name", actual.getName());

        assertSame(protoInstrumentationScope.getAttributesList(), actual.getAttributesList());

        assertSame(protoMetric, protoSpanAdapter.getUpdatedSpan());
        assertSame(protoResource, protoSpanAdapter.getUpdatedResource());

    }

 */
}
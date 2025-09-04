package io.github.pointertrace.siglet.container.eventloop.accumulator;

import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


@Disabled
class SpansAccumulatorTest {

    private Resource resource1;

    private Resource resource2;

    private InstrumentationScope instrumentationScope1;

    private InstrumentationScope instrumentationScope2;

    private Span span1;

    private Span span2;

    private Metric metric1;

    private Metric metric2;

    private ProtoSpanAdapter protoSpanAdapter1;

    private ProtoSpanAdapter protoSpanAdapter2;

    private ProtoMetricAdapter protoMetricAdapter1;

    private ProtoMetricAdapter protoMetricAdapter2;


    @BeforeEach
    void setUp() {
        resource1 = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .build();

        resource2 = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .build();

        instrumentationScope1 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope1")
                .build();

        instrumentationScope2 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope2")
                .build();

        span1 = Span.newBuilder().setName("span1").build();

        span2 = Span.newBuilder().setName("span2").build();

        metric1 = Metric.newBuilder()
                .setName("metric1")
                .build();

        metric2 = Metric.newBuilder()
                .setName("metric2")
                .build();

//        signalsAggregator = new SignalsAggregator();

    }

    @Test
    void add_span_sameResource_sameInstrumentation() {

        protoSpanAdapter1 = new ProtoSpanAdapter().recycle(span1, resource1, instrumentationScope1);

        protoSpanAdapter2 = new ProtoSpanAdapter().recycle(span2, resource1, instrumentationScope1);

//        signalsAggregator.add(protoSpanAdapter1);
//        signalsAggregator.add(protoSpanAdapter2);

        List<ResourceSpans> resourceSpansList = new ArrayList<>();

//        signalsAggregator.consumeSpansBuilder(mb -> resourceSpansList.add(mb.build()));
//
        assertEquals(1, resourceSpansList.size());

        ResourceSpans resourceSpans = resourceSpansList.getFirst();

        assertSame(resource1, resourceSpans.getResource());

        assertEquals(1, resourceSpans.getScopeSpansList().size());
        assertSame(instrumentationScope1, resourceSpans.getScopeSpansList().getFirst().getScope());


        assertEquals(2, resourceSpans.getScopeSpansList().getFirst().getSpansList().size());
        assertSame(span1, resourceSpans.getScopeSpansList().getFirst().getSpansList().getFirst());
        assertSame(span2, resourceSpans.getScopeSpansList().getFirst().getSpansList().get(1));

    }

    @Test
    void add_span_sameResource_twoInstrumentation() {

        protoSpanAdapter1 = new ProtoSpanAdapter().recycle(span1, resource1, instrumentationScope1);

        protoSpanAdapter2 = new ProtoSpanAdapter().recycle(span2, resource1, instrumentationScope2);

//        signalsAggregator.add(protoSpanAdapter1);
//        signalsAggregator.add(protoSpanAdapter2);

        List<ResourceSpans> resourceSpansList = new ArrayList<>();

//        signalsAggregator.consumeSpansBuilder(mb -> resourceSpansList.add(mb.build()));

        assertEquals(1, resourceSpansList.size());

        ResourceSpans resourceSpans = resourceSpansList.getFirst();

        assertSame(resource1, resourceSpans.getResource());

        assertEquals(2, resourceSpans.getScopeSpansList().size());

        assertSame(instrumentationScope1, resourceSpans.getScopeSpansList().getFirst().getScope());
        assertEquals(1, resourceSpans.getScopeSpansList().getFirst().getSpansList().size());
        assertSame(span1, resourceSpans.getScopeSpansList().getFirst().getSpansList().getFirst());

        assertSame(instrumentationScope2, resourceSpans.getScopeSpansList().get(1).getScope());
        assertEquals(1, resourceSpans.getScopeSpansList().get(1).getSpansList().size());
        assertSame(span2, resourceSpans.getScopeSpansList().get(1).getSpansList().getFirst());

    }

    @Test
    void add_span_twoResource_twoInstrumentation() {

        protoSpanAdapter1 = new ProtoSpanAdapter().recycle(span1, resource1, instrumentationScope1);

        protoSpanAdapter2 = new ProtoSpanAdapter().recycle(span2, resource2, instrumentationScope2);

//        signalsAggregator.add(protoSpanAdapter1);
//        signalsAggregator.add(protoSpanAdapter2);

        List<ResourceSpans> resourceSpansList = new ArrayList<>();

//        signalsAggregator.consumeSpansBuilder(mb -> resourceSpansList.add(mb.build()));

        assertEquals(2, resourceSpansList.size());

        ResourceSpans resourceSpans1 = resourceSpansList.getFirst();

        assertSame(resource1, resourceSpans1.getResource());

        assertEquals(1, resourceSpans1.getScopeSpansList().size());

        assertSame(instrumentationScope1, resourceSpans1.getScopeSpansList().getFirst().getScope());
        assertEquals(1, resourceSpans1.getScopeSpansList().getFirst().getSpansList().size());
        assertSame(span1, resourceSpans1.getScopeSpansList().getFirst().getSpansList().getFirst());


        ResourceSpans resourceSpans2 = resourceSpansList.get(1);

        assertSame(resource2, resourceSpans2.getResource());

        assertEquals(1, resourceSpans2.getScopeSpansList().size());

        assertSame(instrumentationScope2, resourceSpans2.getScopeSpansList().getFirst().getScope());
        assertEquals(1, resourceSpans2.getScopeSpansList().getFirst().getSpansList().size());
        assertSame(span2, resourceSpans2.getScopeSpansList().getFirst().getSpansList().getFirst());

    }


    @Test
    void add_metric_sameResource_sameInstrumentation() {

        protoMetricAdapter1 = new ProtoMetricAdapter().recycle(metric1, resource1, instrumentationScope1);

        protoMetricAdapter2 = new ProtoMetricAdapter().recycle(metric2, resource1, instrumentationScope1);

//        signalsAggregator.add(protoMetricAdapter1);
//        signalsAggregator.add(protoMetricAdapter2);

        List<ResourceMetrics> resourceMetricsList = new ArrayList<>();

//        signalsAggregator.consumeMetricsBuilder(mb -> resourceMetricsList.add(mb.build()));

        assertEquals(1, resourceMetricsList.size());

        ResourceMetrics resourceMetrics = resourceMetricsList.getFirst();

        assertSame(resource1, resourceMetrics.getResource());

        assertEquals(1, resourceMetrics.getScopeMetricsList().size());
        assertSame(instrumentationScope1, resourceMetrics.getScopeMetricsList().getFirst().getScope());


        assertEquals(2, resourceMetrics.getScopeMetricsList().getFirst().getMetricsList().size());
        assertSame(metric1, resourceMetrics.getScopeMetricsList().getFirst().getMetricsList().getFirst());
        assertSame(metric2, resourceMetrics.getScopeMetricsList().getFirst().getMetricsList().get(1));

    }

    @Test
    void add_metric_sameResource_twoInstrumentation() {

        protoMetricAdapter1 = new ProtoMetricAdapter().recycle(metric1, resource1, instrumentationScope1);

        protoMetricAdapter2 = new ProtoMetricAdapter().recycle(metric2, resource1, instrumentationScope2);

//        signalsAggregator.add(protoMetricAdapter2);
//        signalsAggregator.add(protoMetricAdapter2);

        List<ResourceMetrics> resourceMetricsList = new ArrayList<>();

//        signalsAggregator.consumeMetricsBuilder(mb -> resourceMetricsList.add(mb.build()));

        assertEquals(1, resourceMetricsList.size());

        ResourceMetrics resourceMetrics = resourceMetricsList.getFirst();

        assertSame(resource1, resourceMetrics.getResource());

        assertEquals(2, resourceMetrics.getScopeMetricsList().size());

        assertSame(instrumentationScope1, resourceMetrics.getScopeMetricsList().getFirst().getScope());
        assertEquals(1, resourceMetrics.getScopeMetricsList().getFirst().getMetricsList().size());
        assertSame(metric1, resourceMetrics.getScopeMetricsList().getFirst().getMetricsList().getFirst());

        assertSame(instrumentationScope2, resourceMetrics.getScopeMetricsList().get(1).getScope());
        assertEquals(1, resourceMetrics.getScopeMetricsList().get(1).getMetricsList().size());
        assertSame(metric2, resourceMetrics.getScopeMetricsList().get(1).getMetricsList().getFirst());

    }

    @Test
    void add_metric_twoResource_twoInstrumentation() {

        protoMetricAdapter1 = new ProtoMetricAdapter().recycle(metric1, resource1, instrumentationScope1);

        protoMetricAdapter2 = new ProtoMetricAdapter().recycle(metric2, resource2, instrumentationScope2);

//        signalsAggregator.add(protoMetricAdapter1);
//        signalsAggregator.add(protoMetricAdapter2);

        List<ResourceMetrics> resourceMetricsList = new ArrayList<>();

//        signalsAggregator.consumeMetricsBuilder(mb -> resourceMetricsList.add(mb.build()));

        assertEquals(2, resourceMetricsList.size());

        ResourceMetrics resourceMetrics1 = resourceMetricsList.getFirst();

        assertSame(resource1, resourceMetrics1.getResource());

        assertEquals(1, resourceMetrics1.getScopeMetricsList().size());

        assertSame(instrumentationScope1, resourceMetrics1.getScopeMetricsList().getFirst().getScope());
        assertEquals(1, resourceMetrics1.getScopeMetricsList().getFirst().getMetricsList().size());
        assertSame(metric1, resourceMetrics1.getScopeMetricsList().getFirst().getMetricsList().getFirst());


        ResourceMetrics resourceMetrics2 = resourceMetricsList.get(1);

        assertSame(resource2, resourceMetrics2.getResource());

        assertEquals(1, resourceMetrics2.getScopeMetricsList().size());

        assertSame(instrumentationScope2, resourceMetrics2.getScopeMetricsList().getFirst().getScope());
        assertEquals(1, resourceMetrics2.getScopeMetricsList().getFirst().getMetricsList().size());
        assertSame(metric2, resourceMetrics2.getScopeMetricsList().getFirst().getMetricsList().getFirst());

    }

}
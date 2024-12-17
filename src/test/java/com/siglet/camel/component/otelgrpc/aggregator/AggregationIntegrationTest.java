package com.siglet.camel.component.otelgrpc.aggregator;

import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.ResourceMetrics;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AggregationIntegrationTest extends CamelTestSupport {

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
    void setUpTest() {
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

        protoSpanAdapter1 = new ProtoSpanAdapter(span1, resource1, instrumentationScope1, true);

        protoSpanAdapter2 = new ProtoSpanAdapter(span2, resource1, instrumentationScope1, true);

        protoMetricAdapter1 = new ProtoMetricAdapter(metric1, resource1, instrumentationScope1, true);

        protoMetricAdapter2 = new ProtoMetricAdapter(metric2, resource2, instrumentationScope2, true);

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .aggregate(constant(true), new SignalAggregationStrategy())
                        .completionSize(4)
                        .to("mock:output");
            }
        };
    }

    @Test
    public void aggregate() throws InterruptedException {
        template.sendBody("direct:start", protoSpanAdapter1);
        template.sendBody("direct:start", protoSpanAdapter2);
        template.sendBody("direct:start", protoMetricAdapter1);
        template.sendBody("direct:start", protoMetricAdapter2);

        MockEndpoint.assertIsSatisfied(context);

        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);

        assertEquals(1, getMockEndpoint("mock:output").getExchanges().size());

        SignalsAggregator signalsAggregator = assertInstanceOf(SignalsAggregator.class,
                getMockEndpoint("mock:output").getExchanges().getFirst().getIn().getBody());

        List<ResourceSpans> resourceSpansList = new ArrayList<>();
        signalsAggregator.consumeSpansBuilder((mb)-> resourceSpansList.add(mb.build()));

        // spans
        assertEquals(1, resourceSpansList.size());

        ResourceSpans resourceSpans = resourceSpansList.getFirst();

        assertSame(resource1, resourceSpans.getResource());

        assertEquals(1, resourceSpans.getScopeSpansList().size());
        assertSame(instrumentationScope1, resourceSpans.getScopeSpansList().getFirst().getScope());


        assertEquals(2, resourceSpans.getScopeSpansList().getFirst().getSpansList().size());
        assertSame(span1, resourceSpans.getScopeSpansList().getFirst().getSpansList().getFirst());
        assertSame(span2, resourceSpans.getScopeSpansList().getFirst().getSpansList().get(1));

        List<ResourceMetrics> resourceMetricsList = new ArrayList<>();
        signalsAggregator.consumeMetricsBuilder((mb)-> resourceMetricsList.add(mb.build()));

        // metrics
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

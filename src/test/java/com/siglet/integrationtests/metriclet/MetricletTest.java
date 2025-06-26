package com.siglet.integrationtests.metriclet;

import com.siglet.container.Siglet;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.Test;

class MetricletTest {

    @Test
    void testSimple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  signal: metric
                  from: receiver
                  start: metriclet
                  siglets:
                  - name: metriclet
                    kind: metriclet
                    to: exporter
                    type: groovy-action
                    config:
                      action: thisSignal.gauge.dataPoints[0].asLong = thisSignal.gauge.dataPoints[0].asLong * 10
                """;

        Siglet siglet = new Siglet(yaml);

        Metric metric = Metric.newBuilder()
                .setName("test-metric")
                .setDescription("metric to test process metrics")
                .setUnit("ms")
                .setGauge(Gauge.newBuilder()
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setAsInt(1)
                        .build())
                .build())
        .build();

        Resource resource = Resource.newBuilder() .build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();

        ProtoMetricAdapter protoMetricAdapter = new ProtoMetricAdapter(metric, resource, instrumentationScope);
//        template.sendBody("direct:start", protoMetricAdapter);
//
//
//        MockEndpoint mock = getMockEndpoint("mock:output");
//
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        var metricAdapter = assertInstanceOf(ProtoMetricAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
//        var gaugeAdapter = assertInstanceOf(ProtoGaugeAdapter.class,  metricAdapter.getData());
//        assertEquals(1, gaugeAdapter.getDataPoints().getSize());
//        assertEquals(10, gaugeAdapter.getDataPoints().getAt(0).getAsLong());

    }

    // todo trocar por metrica
//    @Test
    void testMultiple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: first-exporter
                - debug: second-exporter
                pipelines:
                - trace: pipeline
                  from: receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    type: baseEventloopProcessor
                    config:
                      action: thisSignal.name = thisSignal.name +"-suffix"
                """;

        Siglet siglet = new Siglet(yaml);
        siglet.start();


        Span span = Span.newBuilder().setName("span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(span, resource, instrumentationScope, true);
//        template.sendBody("direct:start", protoSpanAdapter);
//
//
//        MockEndpoint mock = getMockEndpoint("mock:first-output");
//
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        var spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals("span-name-suffix", spanAdapter.getName());
//
//
//        mock = getMockEndpoint("mock:second-output");
//
//        mock.expectedMessageCount(1);
//
//
//        assertEquals(1, mock.getExchanges().size());
//        spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
//        assertEquals("span-name-suffix", spanAdapter.getName());
    }
}

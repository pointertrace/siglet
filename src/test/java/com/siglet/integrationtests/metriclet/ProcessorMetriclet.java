package com.siglet.integrationtests.metriclet;

import com.siglet.config.Config;
import com.siglet.config.ConfigFactory;
import com.siglet.data.adapter.metric.ProtoGaugeAdapter;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ProcessorMetriclet extends CamelTestSupport {

    @Test
    public void testSimple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: exporter
                  address: mock:output
                pipelines:
                - metric: pipeline
                  from: receiver
                  start: metriclet
                  pipeline:
                  - metriclet: metriclet
                    to: exporter
                    type: processor
                    config:
                      action: metric.data.dataPoints.get(0).asLong = metric.data.dataPoints.get(0).asLong * 10
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


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

        ProtoMetricAdapter protoMetricAdapter = new ProtoMetricAdapter(metric, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoMetricAdapter);


        MockEndpoint mock = getMockEndpoint("mock:output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var metricAdapter = assertInstanceOf(ProtoMetricAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        var gaugeAdapter = assertInstanceOf(ProtoGaugeAdapter.class,  metricAdapter.getData());
        assertEquals(1, gaugeAdapter.getDataPoints().getSize());
        assertEquals(10, gaugeAdapter.getDataPoints().get(0).getAsLong());

    }

    @Test
    public void testMultiple() throws Exception {


        String yaml = """
                receivers:
                - debug: receiver
                  address: direct:start
                exporters:
                - debug: first-exporter
                  address: mock:first-output
                - debug: second-exporter
                  address: mock:second-output
                pipelines:
                - trace: pipeline
                  from: receiver
                  start: spanlet
                  pipeline:
                  - spanlet: spanlet
                    to:
                    - first-exporter
                    - second-exporter
                    type: processor
                    config:
                      action: span.name = span.name +"-suffix"
                """;

        ConfigFactory configFactory = new ConfigFactory();

        Config config = configFactory.create(yaml);

        context.addRoutes(config.getRouteBuilder());


        Span span = Span.newBuilder().setName("span-name").build();
        Resource resource = Resource.newBuilder().build();
        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder().build();
        ProtoSpanAdapter protoSpanAdapter = new ProtoSpanAdapter(span, resource, instrumentationScope, true);
        template.sendBody("direct:start", protoSpanAdapter);


        MockEndpoint mock = getMockEndpoint("mock:first-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        var spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("span-name-suffix", spanAdapter.getName());


        mock = getMockEndpoint("mock:second-output");

        mock.expectedMessageCount(1);


        assertEquals(1, mock.getExchanges().size());
        spanAdapter = assertInstanceOf(ProtoSpanAdapter.class, mock.getExchanges().getFirst().getIn().getBody());
        assertEquals("span-name-suffix", spanAdapter.getName());
    }
}

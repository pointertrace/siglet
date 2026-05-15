package io.github.pointertrace.siglet.impl.eventloop.groovy.impl;

import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.SignalCapabilities;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.action.GroovyActionProcessor;
import io.github.pointertrace.siglet.impl.eventloop.MockSignalDestination;
import io.github.pointertrace.siglet.impl.eventloop.processor.ContextImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.impl.eventloop.processor.result.ResultImpl;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SendExpressionGroovyTest {

    private Resource resource;
    private InstrumentationScope instrumentationScope;
    private SpanAdapter spanAdapter;

    @BeforeEach
    void setUp() {
        resource = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("resource attribute key")
                        .setValue(AnyValue.newBuilder().setStringValue("resource attribute value").build())
                        .build())
                .setDroppedAttributesCount(10)
                .build();

        instrumentationScope = InstrumentationScope.newBuilder()
                .setName("instrumentation scope name")
                .setVersion("instrumentation scope version")
                .addAttributes(KeyValue.newBuilder()
                        .setKey("instrumentation scope attribute key")
                        .setValue(AnyValue.newBuilder().setStringValue("instrumentation scope attribute value").build())
                        .build())
                .build();

        Span span = Span.newBuilder()
                .setName("span name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(2, 3))
                .setKind(Span.SpanKind.SPAN_KIND_SERVER)
                .setDroppedEventsCount(4)
                .setDroppedLinksCount(5)
                .setDroppedAttributesCount(6)
                .setFlags(7)
                .setStartTimeUnixNano(8)
                .setEndTimeUnixNano(9)
                .setStatus(Status.newBuilder()
                        .setCode(Status.StatusCode.STATUS_CODE_OK)
                        .setMessage("status message")
                        .build())
                .addAllAttributes(List.of(
                        KeyValue.newBuilder()
                                .setKey("first attribute key")
                                .setValue(AnyValue.newBuilder().setStringValue("first attribute value").build())
                                .build(),
                        KeyValue.newBuilder()
                                .setKey("second attribute key")
                                .setValue(AnyValue.newBuilder().setStringValue("second attribute value").build())
                                .build()
                ))
                .build();

        spanAdapter = new SpanAdapter(span, resource, instrumentationScope);

    }


    @Test
    void send() {
        String spanScript = """
                
                span {
                    name "new span name"
                }
                send  {
                    newGauge {
                        name "gauge name from " + signal.name
                        description "gauge description"
                        unit "gauge unit"
                        dataPoint {
                            value 200.20
                            timeUnixNano 200
                            flags 2
                            attributes {
                                "second datapoint attribute key" "second datapoint attribute value"
                            }
                        }
                    }
                } to "metric"
                
                send  {
                    newSum {
                        name "sum name from " + signal.name
                        description "sum description"
                        unit "sum unit"
                        monotonic true
                        aggregationTemporality DELTA
                        dataPoint {
                            value 100
                            flags 1
                            attributes {
                                "first attribute key" "first attribute value"
                                "second attribute key" dataPoint.value
                                "third attribute key" "new " + attributes["first attribute key"]
                            }
                        }
                    }
                } to "metric"
                
                proceed("default")
                """;


        GroovyActionProcessor.GroovyActionBaseGroovyProcessor<Object> processor =
                new GroovyActionProcessor.GroovyActionBaseGroovyProcessor<>(
                        new ContextImpl<>(new Object()),
                        ResultFactoryImpl.INSTANCE,
                        spanScript
                );

        ResultImpl result = (ResultImpl) processor.process(spanAdapter);

        MockSignalDestination defaultDestination = new MockSignalDestination("default",
                SignalCapabilities.of(io.github.pointertrace.siglet.api.signal.trace.Span.class));

        MockSignalDestination metricDestination = new MockSignalDestination("metric",
                SignalCapabilities.of(io.github.pointertrace.siglet.api.signal.trace.Span.class));

        result.dispatch(Map.of(), spanAdapter, List.of(defaultDestination, metricDestination));

        assertEquals(1, defaultDestination.getSize());
        SpanAdapter actual = defaultDestination.get(0,SpanAdapter.class);
        assertEquals("new span name", actual.getName());

        assertEquals(2, metricDestination.getSize());
        MetricAdapter gauge = metricDestination.get("gauge name from new span name", MetricAdapter.class);
        assertNotNull(gauge);

        assertTrue(gauge.hasGauge());
        assertSame(instrumentationScope, gauge.getUpdatedScope());
        assertSame(resource, gauge.getUpdatedResource());
        assertEquals(1, gauge.getGauge().getDataPoints().getSize());
        assertTrue(gauge.getGauge().getDataPoints().get(0).hasDoubleValue());
        assertEquals(200.20, gauge.getGauge().getDataPoints().get(0).getAsDouble());
        assertEquals(200, gauge.getGauge().getDataPoints().get(0).getTimeUnixNano());



        MetricAdapter sum = metricDestination.get("sum name from new span name", MetricAdapter.class);
        assertNotNull(sum);

        assertTrue(sum.hasSum());
        assertSame(instrumentationScope, sum.getUpdatedScope());
        assertSame(resource, sum.getUpdatedResource());
        assertEquals("sum name from new span name", sum.getName());
        assertEquals(1, sum.getSum().getDataPoints().getSize());
        assertTrue(sum.getSum().getDataPoints().get(0).hasLongValue());
        assertEquals(100, sum.getSum().getDataPoints().get(0).getAsLong());
    }

}
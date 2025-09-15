package io.github.pointertrace.siglet.container.eventloop.groovy.impl;

import groovy.lang.Script;
import io.github.pointertrace.siglet.api.Result;
import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.Compiler;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.action.BaseGroovyActionProcessor;
import io.github.pointertrace.siglet.container.eventloop.MapSignalDestination;
import io.github.pointertrace.siglet.container.eventloop.processor.ProcessorContextImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultFactoryImpl;
import io.github.pointertrace.siglet.container.eventloop.processor.result.ResultImpl;
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
    private ProtoSpanAdapter spanAdapter;

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

        spanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);

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


        BaseGroovyActionProcessor.GroovyActionBaseEventloopProcessor<Object> processor =
                new BaseGroovyActionProcessor.GroovyActionBaseEventloopProcessor<>(
                        new ProcessorContextImpl<>(new Object()),
                        ResultFactoryImpl.INSTANCE,
                        spanScript
                );

        ResultImpl result = (ResultImpl) processor.process(spanAdapter);

        MapSignalDestination defaultDestination = new MapSignalDestination("default");
        MapSignalDestination metricDestination = new MapSignalDestination("metric");
        result.dispatch(Map.of(), spanAdapter, List.of(defaultDestination, metricDestination));

        assertEquals(1, defaultDestination.signals.size());
        assertNotNull(defaultDestination.signals.get(spanAdapter.getId()));
        ProtoSpanAdapter actual = assertInstanceOf(ProtoSpanAdapter.class,
                defaultDestination.signals.get(spanAdapter.getId()));

        assertEquals("new span name", actual.getName());
        ProtoMetricAdapter gauge = assertInstanceOf(ProtoMetricAdapter.class,
                metricDestination.signals.values().stream()
                        .filter(metric -> metric.getId().contains("gauge"))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("must have a gauge signal")));

        assertTrue(gauge.hasGauge());
        assertSame(instrumentationScope, gauge.getUpdatedInstrumentationScope());
        assertSame(resource, gauge.getUpdatedResource());
        assertEquals("gauge name from new span name", gauge.getName());
        assertEquals(1, gauge.getGauge().getDataPoints().getSize());
        assertTrue(gauge.getGauge().getDataPoints().get(0).hasDoubleValue());
        assertEquals(200.20, gauge.getGauge().getDataPoints().get(0).getAsDouble());


        ProtoMetricAdapter sum = assertInstanceOf(ProtoMetricAdapter.class,
                metricDestination.signals.values().stream()
                        .filter(metric -> metric.getId().contains("sum"))
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("must be a counter signal")));

        assertTrue(sum.hasSum());
        assertSame(instrumentationScope, sum.getUpdatedInstrumentationScope());
        assertSame(resource, sum.getUpdatedResource());
        assertEquals("sum name from new span name", sum.getName());
        assertEquals(1, sum.getSum().getDataPoints().getSize());
        assertTrue(sum.getSum().getDataPoints().get(0).hasLongValue());
        assertEquals(100, sum.getSum().getDataPoints().get(0).getAsLong());
    }

}
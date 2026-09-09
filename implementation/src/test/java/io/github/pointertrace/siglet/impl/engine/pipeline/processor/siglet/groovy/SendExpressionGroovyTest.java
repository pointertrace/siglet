package io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy;

import io.github.pointertrace.siglet.api.Context;
import io.github.pointertrace.siglet.api.Signal;
import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.github.pointertrace.siglet.impl.adapter.metric.MetricAdapter;
import io.github.pointertrace.siglet.impl.adapter.trace.SpanAdapter;
import io.github.pointertrace.siglet.impl.engine.component.SignalEmitterFunction;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.BaseSigletProcessor;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.action.GroovyActionConfig;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.groovy.action.SpanletGroovyActionProcessorType;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ContextImpl;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.siglet.ResultImpl;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class SendExpressionGroovyTest {

    private Resource resource;

    private InstrumentationScope instrumentationScope;

    private SpanAdapter spanAdapter;

    private Context<?> context;

    private List<Signal> defaultDestination;

    private List<Signal> metricDestination;

    private final SignalEmitterFunction signalEmitterFunction = (signal, destination) -> {
        if (destination.equals("default")) {
            defaultDestination.add((Signal) signal);
        } else if (destination.equals("metric")) {
            metricDestination.add((Signal) signal);
        } else {
            throw new IllegalArgumentException("Unknown destination: " + destination);
        }
    };

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

        io.opentelemetry.proto.trace.v1.Span span = io.opentelemetry.proto.trace.v1.Span.newBuilder()
                .setName("span name")
                .setSpanId(AdapterUtils.spanId(1))
                .setTraceId(AdapterUtils.traceId(2, 3))
                .setKind(io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_SERVER)
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

        context = new ContextImpl<>();

         defaultDestination = new ArrayList<>();

         metricDestination = new ArrayList<>();

    }


    @Test
    void send() {
        String script = """
                
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



        GroovyActionConfig groovyActionConfig = new GroovyActionConfig();
        groovyActionConfig.setAction(script);

        BiFunction<Signal, Context<?>, BaseSigletProcessor.ProcessResult> spanletFunction = SpanletGroovyActionProcessorType.createSpanletFunction(groovyActionConfig);

        BaseSigletProcessor.ProcessResult result = spanletFunction.apply(spanAdapter, context);

        ((ResultImpl) result.result()).emit(Map.of(), signalEmitterFunction, spanAdapter);


        assertEquals(1, defaultDestination.size());

        SpanAdapter actual = assertInstanceOf(SpanAdapter.class, defaultDestination.getFirst());
        assertEquals("new span name", actual.getName());

        assertEquals(2, metricDestination.size());
        // fazer um teste sem ordem ---- getFirst e estranho!!!!
        MetricAdapter gauge = assertInstanceOf(MetricAdapter.class, metricDestination.getFirst());
        assertNotNull(gauge);

        assertTrue(gauge.hasGauge());
        assertSame(instrumentationScope, gauge.getUpdatedScope());
        assertSame(resource, gauge.getUpdatedResource());
        assertEquals(1, gauge.getGauge().getDataPoints().getSize());
        assertTrue(gauge.getGauge().getDataPoints().get(0).hasDoubleValue());
        assertEquals(200.20, gauge.getGauge().getDataPoints().get(0).getAsDouble());
        assertEquals(200, gauge.getGauge().getDataPoints().get(0).getTimeUnixNano());



        MetricAdapter sum = assertInstanceOf(MetricAdapter.class, metricDestination.getLast());
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
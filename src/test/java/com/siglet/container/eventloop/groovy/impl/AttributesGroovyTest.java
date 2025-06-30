package com.siglet.container.eventloop.groovy.impl;

import com.siglet.api.Signal;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.pipeline.processor.groovy.Compiler;
import com.siglet.container.engine.pipeline.processor.groovy.ScriptBaseClass;
import com.siglet.container.engine.pipeline.processor.groovy.proxy.NumberDataPointAttributesProxy;
import com.siglet.container.engine.pipeline.processor.groovy.proxy.SpanAttributesProxy;
import groovy.lang.Closure;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.metrics.v1.Gauge;
import io.opentelemetry.proto.metrics.v1.Metric;
import io.opentelemetry.proto.metrics.v1.NumberDataPoint;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AttributesGroovyTest {

    private ProtoSpanAdapter spanAdapter;

    private ProtoMetricAdapter metricAdapter;

    private Compiler compiler;

    @BeforeEach
    public void setUp() {
        List<KeyValue> attributes = List.of(
                KeyValue.newBuilder()
                        .setKey("first attribute key")
                        .setValue(AnyValue.newBuilder().setStringValue("first attribute value").build())
                        .build(),
                KeyValue.newBuilder()
                        .setKey("second attribute key")
                        .setValue(AnyValue.newBuilder().setStringValue("second attribute value").build())
                        .build()
        );
        Resource resource = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("resource attribute key")
                        .setValue(AnyValue.newBuilder().setStringValue("resource attribute value").build())
                        .build())
                .setDroppedAttributesCount(10)
                .build();

        InstrumentationScope instrumentationScope = InstrumentationScope.newBuilder()
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
                .setStatus(io.opentelemetry.proto.trace.v1.Status.newBuilder()
                        .setCode(Status.StatusCode.STATUS_CODE_OK)
                        .setMessage("status message")
                        .build())
                .addAllAttributes(attributes)
                .build();

        Metric metric = Metric.newBuilder()
                .setName("metric name")
                .setUnit("metric unit")
                .setDescription("metric description")
                .setGauge(Gauge.newBuilder()
                        .addDataPoints(NumberDataPoint.newBuilder()
                                .setAsInt(10)
                                .setStartTimeUnixNano(100)
                                .addAllAttributes(attributes)
                                .build())
                        .build())
                .build();

        spanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);

        metricAdapter = new ProtoMetricAdapter(metric, resource, instrumentationScope);

        compiler = new Compiler(AttributesBaseScript.class);
        AttributesBaseScript.spanAdapter = spanAdapter;
        AttributesBaseScript.metricAdapter = metricAdapter;
    }

    @Test
    void spanAttributes() {
        String attributesScript = """
                spanAttributes {
                  remove "first attribute key"
                  "attribute from span" span.name
                  "second attribute key" "new " + attributes["second attribute key"]
                  "attribute from signal" signal.name
                }
                """;

        Script script = compiler.compile(attributesScript);
        compiler.prepareScript(script, spanAdapter);
        script.run();


        ProtoAttributesAdapter attributesAdapter = spanAdapter.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(3, spanAdapter.getAttributes().getSize());
        assertEquals("span name", attributesAdapter.getAsString("attribute from span"));
        assertEquals("new second attribute value", attributesAdapter.getAsString("second attribute key"));
        assertEquals("signal name", attributesAdapter.getAsString("attribute from signal"));
    }

    @Test
    void metricAttributes() {
        String attributesScript = """
                metricAttributes {
                  remove "first attribute key"
                  "attribute from metric" metric.name
                  "attribute from dataPoint" dataPoint.asLong
                  "second attribute key" "new " + attributes["second attribute key"]
                  "attribute from signal" signal.name
                }
                """;

        Script script = compiler.compile(attributesScript);
        compiler.prepareScript(script, spanAdapter);
        script.run();

        ProtoAttributesAdapter attributesAdapter = metricAdapter.getGauge().getDataPoints().getAt(0).getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(4, attributesAdapter.getSize());
        assertEquals("metric name", attributesAdapter.getAsString("attribute from metric"));
        assertEquals(10, attributesAdapter.getAsLong("attribute from dataPoint"));
        assertEquals("new second attribute value", attributesAdapter.getAsString("second attribute key"));
        assertEquals("signal name", attributesAdapter.getAsString("attribute from signal"));
    }

    public abstract static class AttributesBaseScript extends ScriptBaseClass {

        private static ProtoSpanAdapter spanAdapter;
        private static ProtoMetricAdapter metricAdapter;

        public void spanAttributes(Closure<Void> closure) {
            closure.setDelegate(new SpanAttributesProxy(new SignalMock(), spanAdapter));
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
        }

        public void metricAttributes(Closure<Void> closure) {
            closure.setDelegate(new NumberDataPointAttributesProxy(
                    new SignalMock(), metricAdapter, metricAdapter.getGauge().getDataPoints().getAt(0)));
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
        }
    }

    public static class SignalMock implements Signal {
        public String getName() {
            return "signal name";
        }

        @Override
        public String getId() {
            return "SignalMock";
        }
    }


}

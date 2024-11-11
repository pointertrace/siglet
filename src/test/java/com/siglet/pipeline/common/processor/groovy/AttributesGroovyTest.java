package com.siglet.pipeline.common.processor.groovy;

import com.google.protobuf.ByteString;
import com.siglet.data.adapter.AdapterUtils;
import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import com.siglet.pipeline.common.processor.groovy.proxy.NumberDataPointAttributesProxy;
import com.siglet.pipeline.common.processor.groovy.proxy.SpanAttributesProxy;
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

public class AttributesGroovyTest {

    private ProtoSpanAdapter spanAdapter;

    private ProtoMetricAdapter metricAdapter;

    private ShellCreator shellCreator;

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
                .setSpanId(ByteString.copyFrom(AdapterUtils.spanId(1)))
                .setTraceId(ByteString.copyFrom(AdapterUtils.traceId(2, 3)))
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

        spanAdapter = new ProtoSpanAdapter(span, resource, instrumentationScope, true);

        metricAdapter = new ProtoMetricAdapter(metric, resource, instrumentationScope, true);

        shellCreator = new ShellCreator(AttributesBaseScript.class);
        AttributesBaseScript.spanAdapter = spanAdapter;
        AttributesBaseScript.metricAdapter = metricAdapter;
    }

    @Test
    public void spanAttributes() {
        String attributesScript = """
                spanAttributes {
                  remove "first attribute key"
                  "attribute from span" span.name
                  "second attribute key" "new " + attributes["second attribute key"]
                  "attribute from thisSignal" thisSignal.name
                }
                """;

        Script script = shellCreator.compile(attributesScript);
        shellCreator.prepareScript(script, spanAdapter);
        script.run();


        ProtoAttributesAdapter attributesAdapter = spanAdapter.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(3, spanAdapter.getAttributes().getSize());
        assertEquals("span name", attributesAdapter.getAsString("attribute from span"));
        assertEquals("new second attribute value", attributesAdapter.getAsString("second attribute key"));
        assertEquals("thisSignal name", attributesAdapter.getAsString("attribute from thisSignal"));
    }

    @Test
    public void metricAttributes() {
        String attributesScript = """
                metricAttributes {
                  remove "first attribute key"
                  "attribute from metric" metric.name
                  "attribute from dataPoint" dataPoint.asLong
                  "second attribute key" "new " + attributes["second attribute key"]
                  "attribute from thisSignal" thisSignal.name
                }
                """;

        Script script = shellCreator.compile(attributesScript);
        shellCreator.prepareScript(script, spanAdapter);
        script.run();

        ProtoAttributesAdapter attributesAdapter = metricAdapter.getGauge().getDataPoints().getAt(0).getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(4, attributesAdapter.getSize());
        assertEquals("metric name", attributesAdapter.getAsString("attribute from metric"));
        assertEquals(10, attributesAdapter.getAsLong("attribute from dataPoint"));
        assertEquals("new second attribute value", attributesAdapter.getAsString("second attribute key"));
        assertEquals("thisSignal name", attributesAdapter.getAsString("attribute from thisSignal"));
    }

    public abstract static class AttributesBaseScript extends ScriptBaseClass {

        private static ProtoSpanAdapter spanAdapter;
        private static ProtoMetricAdapter metricAdapter;

        public void spanAttributes(Closure<Void> closure) {
            closure.setDelegate(new SpanAttributesProxy(new ThisSignalMock(), spanAdapter));
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
        }

        public void metricAttributes(Closure<Void> closure) {
            closure.setDelegate(new NumberDataPointAttributesProxy(
                    new ThisSignalMock(), metricAdapter, metricAdapter.getGauge().getDataPoints().getAt(0)));
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
            closure.call();
        }
    }

    public static class ThisSignalMock {
        public String getName() {
            return "thisSignal name";
        }
    }


}

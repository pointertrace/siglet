package io.github.pointertrace.siglet.container.eventloop.groovy.impl;

import io.github.pointertrace.siglet.container.adapter.common.ProtoAttributesAdapter;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoMetricAdapter;
import io.github.pointertrace.siglet.container.adapter.metric.ProtoNumberDataPointAdapter;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.Compiler;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GaugeGroovyTest {

    private Resource resource;
    private InstrumentationScope instrumentationScope;
    private Compiler compiler;
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
                .build();

        spanAdapter = new ProtoSpanAdapter().recycle(span, resource, instrumentationScope);

        compiler = new Compiler();

    }


    @Test
    void newGauge() {
        String gaugeScript = """
                newGauge {
                    name "gauge name from " + signal.name
                    description "gauge description"
                    unit "gauge unit"
                    dataPoint {
                        value 100
                        flags 1
                        attributes {
                          "first attribute key" "first attribute value"
                          "second attribute key" dataPoint.value
                          "third attribute key" "new " + attributes["first attribute key"]
                        }
                    }
                    dataPoint {
                        value 200.20
                        flags 2
                        attributes {
                          "second datapoint attribute key" "second datapoint attribute value"
                        }
                    }
                }
                """;

        Script script = compiler.compile(gaugeScript);
        compiler.prepareScript(script, spanAdapter);

        ProtoMetricAdapter newGauge = (ProtoMetricAdapter) script.run();


        assertTrue(newGauge.hasGauge());
        assertEquals("gauge name from span name", newGauge.getName());
        assertEquals("gauge description", newGauge.getDescription());
        assertEquals("gauge unit", newGauge.getUnit());

        assertEquals(2, newGauge.getGauge().getDataPoints().getSize());

        // first data point
        ProtoNumberDataPointAdapter numberDataPoint = newGauge.getGauge().getDataPoints().get(0);
        assertNotNull(numberDataPoint);
        assertEquals(100, numberDataPoint.getAsLong());
        assertEquals(1, numberDataPoint.getFlags());

        ProtoAttributesAdapter attributesAdapter = numberDataPoint.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(3, numberDataPoint.getAttributes().getSize());
        assertEquals("first attribute value", attributesAdapter.getAsString("first attribute key"));
        assertEquals(100, attributesAdapter.getAsLong("second attribute key"));
        assertEquals("new first attribute value", attributesAdapter.getAsString("third attribute key"));

        assertSame(resource, newGauge.getUpdatedResource());
        assertSame(instrumentationScope, newGauge.getUpdatedInstrumentationScope());


        // second datapoint
        numberDataPoint = newGauge.getGauge().getDataPoints().get(1);
        assertNotNull(numberDataPoint);
        assertEquals(200.20, numberDataPoint.getAsDouble());
        assertEquals(2, numberDataPoint.getFlags());

        attributesAdapter = numberDataPoint.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(1, numberDataPoint.getAttributes().getSize());
        assertEquals("second datapoint attribute value",
                attributesAdapter.getAsString("second datapoint attribute key"));

        // resource and instrumentation scope
        assertSame(resource, newGauge.getUpdatedResource());
        assertSame(instrumentationScope, newGauge.getUpdatedInstrumentationScope());
    }
}

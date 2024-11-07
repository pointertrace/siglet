package com.siglet.pipeline.common.processor.groovy;

import com.siglet.data.adapter.common.ProtoAttributesAdapter;
import com.siglet.data.adapter.metric.ProtoMetricAdapter;
import com.siglet.data.adapter.metric.ProtoNumberDataPointAdapter;
import com.siglet.data.adapter.trace.ProtoSpanAdapter;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GaugeGroovyTest {

    private Resource resource;
    private InstrumentationScope instrumentationScope;
    private ShellCreator shellCreator;
    private ProtoSpanAdapter spanAdapter;


    @BeforeEach
    public void setUp() {
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

        spanAdapter = new ProtoSpanAdapter(span, resource, instrumentationScope, true);

        shellCreator = new ShellCreator();

    }


    @Test
    public void newGauge() {
        String gaugeScript = """
                newGauge {
                    name "gauge name from " + thisSignal.name
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

        Script script = shellCreator.createScript(gaugeScript, spanAdapter);

        ProtoMetricAdapter newGauge = (ProtoMetricAdapter) script.run();


        assertTrue(newGauge.hasGauge());
        assertEquals("gauge name from span name", newGauge.getName());
        assertEquals("gauge description", newGauge.getDescription());
        assertEquals("gauge unit", newGauge.getUnit());

        assertEquals(2, newGauge.getGauge().getDataPoints().getSize());

        // first data point
        ProtoNumberDataPointAdapter numberDataPoint = newGauge.getGauge().getDataPoints().getAt(0);
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
        numberDataPoint = newGauge.getGauge().getDataPoints().getAt(1);
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

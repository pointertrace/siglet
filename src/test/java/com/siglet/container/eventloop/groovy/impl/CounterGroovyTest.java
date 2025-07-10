package com.siglet.container.eventloop.groovy.impl;

import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.metric.ProtoMetricAdapter;
import com.siglet.container.adapter.metric.ProtoNumberDataPointAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.engine.pipeline.processor.groovy.Compiler;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CounterGroovyTest {

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

        spanAdapter = new ProtoSpanAdapter().recycle(span,resource,instrumentationScope);

        compiler = new Compiler();

    }



    @Test
    void newCounter()  {
        String counterScript = """
                newCounter {
                    name "counter name " + signal.name
                    description "counter description"
                    unit "counter unit"
                    dataPoint {
                        value 50
                        flags 5
                        attributes {
                            "first attribute key" "first attribute value"
                            "second attribute key" "second attribute value"
                        }
                    }
                }
                """;


        Script script = compiler.compile(counterScript);
        compiler.prepareScript(script, spanAdapter);



        ProtoMetricAdapter newCounter = (ProtoMetricAdapter) script.run();

        assertTrue(newCounter.hasSum());
        assertEquals("counter name span name", newCounter.getName());
        assertEquals("counter description", newCounter.getDescription());
        assertEquals("counter unit", newCounter.getUnit());

        assertEquals(1, newCounter.getSum().getDataPoints().getSize());
        ProtoNumberDataPointAdapter numberDataPoint = newCounter.getSum().getDataPoints().get(0);
        assertNotNull(numberDataPoint);
        assertEquals(50,numberDataPoint.getAsLong());
        assertEquals(5,numberDataPoint.getFlags());

        ProtoAttributesAdapter attributesAdapter = numberDataPoint.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(2, numberDataPoint.getAttributes().getSize());
        assertEquals("first attribute value",attributesAdapter.getAsString("first attribute key") );
        assertEquals("second attribute value",attributesAdapter.getAsString("second attribute key") );


        // resource and instrumentation scope
        assertSame(resource, newCounter.getUpdatedResource());
        assertSame(instrumentationScope, newCounter.getUpdatedInstrumentationScope());
    }
}

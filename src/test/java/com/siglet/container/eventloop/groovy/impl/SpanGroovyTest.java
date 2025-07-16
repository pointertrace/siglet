package com.siglet.container.eventloop.groovy.impl;

import com.siglet.api.data.trace.SpanKind;
import com.siglet.api.data.trace.StatusCode;
import com.siglet.container.adapter.AdapterUtils;
import com.siglet.container.adapter.common.ProtoAttributesAdapter;
import com.siglet.container.adapter.trace.ProtoSpanAdapter;
import com.siglet.container.adapter.trace.ProtoStatusAdapter;
import com.siglet.container.engine.pipeline.processor.groovy.Compiler;
import groovy.lang.Script;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.proto.trace.v1.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpanGroovyTest {

    private Resource resource;
    private InstrumentationScope instrumentationScope;
    private ProtoSpanAdapter spanAdapter;
    private Compiler compiler;

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
                .setStatus(io.opentelemetry.proto.trace.v1.Status.newBuilder()
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

        compiler = new Compiler();
    }


    @Test
    void span() {
        String spanScript = """
                span {
                    name "new span name"
                    spanId 10
                    traceId 20,30
                    kind "SERVER"
                    droppedEventsCount 40
                    droppedLinksCount 50
                    droppedAttributesCount 60
                    flags 70
                    startTimeUnixNano 80
                    endTimeUnixNano 90
                    status {
                        message: "status message"
                    }
                    attributes {
                        "first attribute key" "new first attribute value"
                    }
                }
                """;


        Script script = compiler.compile(spanScript);
        compiler.prepareScript(script, spanAdapter);

        script.run();
        assertEquals("new span name", spanAdapter.getName());
        assertEquals(10, spanAdapter.getSpanId());
        assertEquals(20, spanAdapter.getTraceIdHigh());
        assertEquals(30, spanAdapter.getTraceIdLow());
        assertEquals(SpanKind.SERVER, spanAdapter.getKind());
        assertEquals(40, spanAdapter.getDroppedEventsCount());
        assertEquals(50, spanAdapter.getDroppedLinksCount());
        assertEquals(60, spanAdapter.getDroppedAttributesCount());
        assertEquals(70, spanAdapter.getFlags());
        assertEquals(80, spanAdapter.getStartTimeUnixNano());
        assertEquals(90, spanAdapter.getEndTimeUnixNano());

        ProtoStatusAdapter statusAdapter = spanAdapter.getStatus();
        assertEquals(StatusCode.OK, statusAdapter.getCode());
        assertEquals("status message", statusAdapter.getStatusMessage());

        ProtoAttributesAdapter attributesAdapter = spanAdapter.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(2, spanAdapter.getAttributes().getSize());
        assertEquals("new first attribute value", attributesAdapter.getAsString("first attribute key"));
        assertEquals("second attribute value", attributesAdapter.getAsString("second attribute key"));

        // resource and instrumentation scope
        assertSame(resource, spanAdapter.getUpdatedResource());
        assertSame(instrumentationScope, spanAdapter.getUpdatedInstrumentationScope());
    }

    @Test
    void spanWithThisSignal() {
        String spanScript = """
                span {
                    name "new " + signal.name
                    spanId signal.spanId + 10
                    traceId signal.traceIdHigh + 20, signal.traceIdLow + 30
                    droppedEventsCount signal.droppedEventsCount + 40
                    droppedLinksCount signal.droppedLinksCount + 50
                    droppedAttributesCount signal.droppedAttributesCount + 60
                    flags signal.flags + 70
                    startTimeUnixNano signal.startTimeUnixNano + 80
                    endTimeUnixNano signal.endTimeUnixNano + 90
                    status {
                        code "OK"
                        message "new " + signal.status.statusMessage
                    }
                    attributes {
                        "first attribute key" "new first attribute value"
                        remove "second attribute key"
                        "third attribute key" "third attribute value"
                
                
                
                    }
                }
                """;

        Script script = compiler.compile(spanScript);
        compiler.prepareScript(script, spanAdapter);

        script.run();

        assertEquals("new span name", spanAdapter.getName());
        assertEquals(11, spanAdapter.getSpanId());
        assertEquals(22, spanAdapter.getTraceIdHigh());
        assertEquals(33, spanAdapter.getTraceIdLow());
        assertEquals(44, spanAdapter.getDroppedEventsCount());
        assertEquals(55, spanAdapter.getDroppedLinksCount());
        assertEquals(66, spanAdapter.getDroppedAttributesCount());

        assertEquals(77, spanAdapter.getFlags());
        assertEquals(88, spanAdapter.getStartTimeUnixNano());
        assertEquals(99, spanAdapter.getEndTimeUnixNano());

        ProtoStatusAdapter statusAdapter = spanAdapter.getStatus();
        assertEquals("new status message", statusAdapter.getStatusMessage());

        ProtoAttributesAdapter attributesAdapter = spanAdapter.getAttributes();
        assertNotNull(attributesAdapter);
        assertEquals(2, spanAdapter.getAttributes().getSize());
        assertEquals("new first attribute value", attributesAdapter.getAsString("first attribute key"));
        assertEquals("third attribute value", attributesAdapter.getAsString("third attribute key"));

        // resource and instrumentation scope
        assertSame(resource, spanAdapter.getUpdatedResource());
        assertSame(instrumentationScope, spanAdapter.getUpdatedInstrumentationScope());
    }
}

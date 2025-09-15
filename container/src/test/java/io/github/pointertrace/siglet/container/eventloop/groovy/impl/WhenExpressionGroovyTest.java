package io.github.pointertrace.siglet.container.eventloop.groovy.impl;

import io.github.pointertrace.siglet.container.adapter.AdapterUtils;
import io.github.pointertrace.siglet.container.adapter.trace.ProtoSpanAdapter;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.groovy.Compiler;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhenExpressionGroovyTest {

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

        compiler = new Compiler();
    }


    @Test
    void WhenTrue() {
        String spanScript = """
                when { signal.name == "span name" } then {
                  span {
                    name "new span name"
                  }
                }
                """;


        Script script = compiler.compile(spanScript);
        compiler.prepareScript(script, spanAdapter, null);

        script.run();
        assertEquals("new span name", spanAdapter.getName());
    }

    @Test
    void WhenFalse() {
        String spanScript = """
                when { signal.name != "span name" } then {
                  span {
                    name "new span name"
                  }
                }
                """;

        Script script = compiler.compile(spanScript);
        compiler.prepareScript(script, spanAdapter, null);

        script.run();
        assertEquals("span name", spanAdapter.getName());
    }
}

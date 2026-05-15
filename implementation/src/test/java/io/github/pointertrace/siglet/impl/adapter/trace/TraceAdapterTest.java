package io.github.pointertrace.siglet.impl.adapter.trace;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TraceAdapterTest {
    @Test
    void shouldManageSpansById() {
        io.opentelemetry.proto.trace.v1.Span spanProto = io.opentelemetry.proto.trace.v1.Span.newBuilder()
            .setTraceId(com.google.protobuf.ByteString.copyFrom(new byte[16]))
            .setSpanId(com.google.protobuf.ByteString.copyFrom(new byte[8]))
            .build();
        SpanAdapter spanAdapter = new SpanAdapter(
            spanProto,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());
        TraceAdapter traceAdapter = new TraceAdapter(0L, 0L, Collections.singletonList(spanAdapter));
        assertEquals(1, traceAdapter.getSize());
        assertNotNull(traceAdapter.get(spanAdapter.getSpanId()));
        assertTrue(traceAdapter.remove(spanAdapter.getSpanId()));
        assertEquals(0, traceAdapter.getSize());
    }
    @Test
    void shouldBeFluent() {
        io.opentelemetry.proto.trace.v1.Span spanProto = io.opentelemetry.proto.trace.v1.Span.newBuilder()
            .setTraceId(com.google.protobuf.ByteString.copyFrom(new byte[16]))
            .setSpanId(com.google.protobuf.ByteString.copyFrom(new byte[8]))
            .build();
        SpanAdapter spanAdapter = new SpanAdapter(
            spanProto,
            io.opentelemetry.proto.resource.v1.Resource.getDefaultInstance(),
            io.opentelemetry.proto.common.v1.InstrumentationScope.getDefaultInstance());

        // Test fluency
        SpanAdapter s = spanAdapter.setName("test").setDroppedAttributesCount(10);
        assertSame(spanAdapter, s);
        assertEquals("test", spanAdapter.getName());

        StatusAdapter statusAdapter = spanAdapter.getStatus();
        StatusAdapter sa = statusAdapter.setCode(io.github.pointertrace.siglet.api.signal.trace.StatusCode.ERROR).setStatusMessage("error");
        assertSame(statusAdapter, sa);
    }
}

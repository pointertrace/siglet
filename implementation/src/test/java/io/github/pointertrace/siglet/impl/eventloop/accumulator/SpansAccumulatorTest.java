package io.github.pointertrace.siglet.impl.eventloop.accumulator;

import io.github.pointertrace.siglet.impl.engine.exporter.grpc.accumulator.span.SpansAccumulator;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


class SpansAccumulatorTest {

    private Resource resource1;

    private Resource resource2;

    private InstrumentationScope instrumentationScope1;

    private InstrumentationScope instrumentationScope2;

    private Span span1;

    private Span span2;

    private Span span3;

    private Span span4;

    private SpansAccumulator spansAccumulator;

    @BeforeEach
    void setUp() {
        resource1 = Resource.newBuilder()
                .setDroppedAttributesCount(1)
                .build();

        resource2 = Resource.newBuilder()
                .setDroppedAttributesCount(2)
                .build();

        instrumentationScope1 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope1")
                .build();

        instrumentationScope2 = InstrumentationScope.newBuilder()
                .setName("instrumentationScope2")
                .build();

        span1 = Span.newBuilder().setName("span1").build();

        span2 = Span.newBuilder().setName("span2").build();

        span3 = Span.newBuilder().setName("span3").build();

        span4 = Span.newBuilder().setName("span4").build();


        spansAccumulator = new SpansAccumulator();

    }

    @Test
    void add_span_sameResource_sameInstrumentation() {

        spansAccumulator.add(span1, instrumentationScope1, resource1);
        spansAccumulator.add(span2, instrumentationScope1, resource1);
        spansAccumulator.add(span3, instrumentationScope1, resource1);
        spansAccumulator.add(span4, instrumentationScope1, resource1);

        ExportTraceServiceRequest request = spansAccumulator.getExportTraceServiceRequest();
        checkPath(request, resource1, instrumentationScope1, span1);
        checkPath(request, resource1, instrumentationScope1, span2);
        checkPath(request, resource1, instrumentationScope1, span3);
        checkPath(request, resource1, instrumentationScope1, span4);

        checkResourceCount(request, 1);
        checkInstrumentationScopeCount(request, resource1, 1);
        checkSpanCount(request, resource1, instrumentationScope1, 4);

    }

    @Test
    void add_span_sameResource_twoInstrumentation() {

        spansAccumulator.add(span1, instrumentationScope1, resource1);
        spansAccumulator.add(span2, instrumentationScope1, resource1);
        spansAccumulator.add(span3, instrumentationScope2, resource1);
        spansAccumulator.add(span4, instrumentationScope2, resource1);


        ExportTraceServiceRequest request = spansAccumulator.getExportTraceServiceRequest();
        checkPath(request, resource1, instrumentationScope1, span1);
        checkPath(request, resource1, instrumentationScope1, span2);
        checkPath(request, resource1, instrumentationScope2, span3);
        checkPath(request, resource1, instrumentationScope2, span4);

        checkResourceCount(request, 1);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkSpanCount(request, resource1, instrumentationScope1, 2);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkSpanCount(request, resource1, instrumentationScope2, 2);
    }

    @Test
    void add_span_twoResource_twoInstrumentation() {



        spansAccumulator.add(span1, instrumentationScope1, resource1);
        spansAccumulator.add(span2, instrumentationScope1, resource2);
        spansAccumulator.add(span3, instrumentationScope2, resource1);
        spansAccumulator.add(span4, instrumentationScope2, resource2);


        ExportTraceServiceRequest request = spansAccumulator.getExportTraceServiceRequest();
        checkPath(request, resource1, instrumentationScope1, span1);
        checkPath(request, resource2, instrumentationScope1, span2);
        checkPath(request, resource1, instrumentationScope2, span3);
        checkPath(request, resource2, instrumentationScope2, span4);

        checkResourceCount(request, 2);

        checkInstrumentationScopeCount(request, resource1, 2);
        checkSpanCount(request, resource1, instrumentationScope1, 1);
        checkSpanCount(request, resource1, instrumentationScope2, 1);

        checkInstrumentationScopeCount(request, resource2, 2);
        checkSpanCount(request, resource2, instrumentationScope1, 1);
        checkSpanCount(request, resource2, instrumentationScope2, 1);




    }


    private void checkPath(ExportTraceServiceRequest request, Resource resource, InstrumentationScope scope, Span span) {
        ResourceSpans resourceSpans = request.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"));

        ScopeSpans scopeSpans = resourceSpans.getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(scope))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("scope not found!"));

        Span foundSpan = scopeSpans.getSpansList().stream()
                .filter(s -> s.equals(span))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("span not found!"));

    }


    private void checkResourceCount(ExportTraceServiceRequest request, int resourceCount) {
        assertEquals(resourceCount, request.getResourceSpansList().size());
    }

    private void checkInstrumentationScopeCount(ExportTraceServiceRequest request, Resource resource, int scopeCount) {

        assertEquals(scopeCount, request.getResourceSpansList().stream()
                .filter(rs -> rs.getResource() == resource)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"))
                .getScopeSpansList().size());
    }

    private void checkSpanCount(ExportTraceServiceRequest request, Resource resource, InstrumentationScope scope, int spanCount) {

        assertEquals(spanCount,  request.getResourceSpansList().stream()
                .filter(rs -> rs.getResource() == resource)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("resource not found!"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope() == scope)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("scope not found!"))
                .getSpansList().size());
    }
}
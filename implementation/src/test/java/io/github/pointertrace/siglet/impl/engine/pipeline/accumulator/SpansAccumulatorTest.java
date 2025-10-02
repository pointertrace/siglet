package io.github.pointertrace.siglet.impl.engine.pipeline.accumulator;

import io.github.pointertrace.siglet.impl.adapter.AdapterUtils;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpansAccumulatorTest {


    private SpansAccumulator spansAccumulator;

    private Span firstSpan;

    private Span secondSpan;

    private Span thirdSpan;

    private Span forthSpan;

    private InstrumentationScope firstScope;

    private InstrumentationScope secondScope;

    private Resource firstResource;

    private Resource secondResource;


    @BeforeEach
    void setUp() {

        spansAccumulator = new SpansAccumulator();

        firstSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(1))
                .setName("first-span")
                .build();

        secondSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(2))
                .setName("second-span")
                .build();

        thirdSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(3))
                .setName("third-span")
                .build();

        forthSpan = Span.newBuilder()
                .setTraceId(AdapterUtils.traceId(0, 1))
                .setSpanId(AdapterUtils.spanId(4))
                .setName("forth-span")
                .build();

        firstScope = InstrumentationScope.newBuilder()
                .setName("first-scope")
                .build();

        secondScope = InstrumentationScope.newBuilder()
                .setName("second-scope")
                .build();

        firstResource = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("service.name")
                        .setValue(AnyValue.newBuilder().setStringValue("first-resource").build())
                        .build())
                .build();

        secondResource = Resource.newBuilder()
                .addAttributes(KeyValue.newBuilder()
                        .setKey("service.name")
                        .setValue(AnyValue.newBuilder().setStringValue("second-resource").build())
                        .build())
                .build();

    }

    @Test
    void oneSpanOneScopeOneResource() {

        spansAccumulator.add(firstSpan, firstScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                spansAccumulator.getExportTraceServiceRequest();

        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());
        assertTrue(findResource(exportTraceServiceRequest, firstResource));
        assertTrue(findScope(exportTraceServiceRequest, firstResource, firstScope));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, firstSpan));

        assertEquals(1, countResources(exportTraceServiceRequest));
        assertEquals(1, countScopes(exportTraceServiceRequest,firstResource));
        assertEquals(1, countSpans(exportTraceServiceRequest,firstResource, firstScope));
    }


    @Test
    void twoSpanOneScopeOneResource() {

        spansAccumulator.add(firstSpan, firstScope, firstResource);
        spansAccumulator.add(secondSpan, firstScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                spansAccumulator.getExportTraceServiceRequest();


        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());
        assertTrue(findResource(exportTraceServiceRequest, firstResource));
        assertEquals(1, countResources(exportTraceServiceRequest));
        assertTrue(findScope(exportTraceServiceRequest, firstResource, firstScope));
        assertEquals(1, countScopes(exportTraceServiceRequest, firstResource));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, firstSpan));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, secondSpan));
        assertEquals(2, countSpans(exportTraceServiceRequest, firstResource, firstScope));

    }

    @Test
    void twoSpanTwoScopeOneResource() {

        spansAccumulator.add(firstSpan, firstScope, firstResource);
        spansAccumulator.add(secondSpan, firstScope, firstResource);
        spansAccumulator.add(thirdSpan, secondScope, firstResource);
        spansAccumulator.add(forthSpan, secondScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                spansAccumulator.getExportTraceServiceRequest();

        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());

        assertTrue(findResource(exportTraceServiceRequest, firstResource));
        assertEquals(1, countResources(exportTraceServiceRequest));

        assertTrue(findScope(exportTraceServiceRequest, firstResource, firstScope));
        assertTrue(findScope(exportTraceServiceRequest, firstResource, secondScope));
        assertEquals(2, countScopes(exportTraceServiceRequest, firstResource));

        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, firstSpan));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, secondSpan));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, secondScope, thirdSpan));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, secondScope, forthSpan));
        assertEquals(2, countSpans(exportTraceServiceRequest, firstResource, firstScope));
        assertEquals(2, countSpans(exportTraceServiceRequest, firstResource, secondScope));

    }

    @Test
    void twoSpanTwoScopeTwoResource() {

        spansAccumulator.add(firstSpan, firstScope, firstResource);
        spansAccumulator.add(secondSpan, secondScope, firstResource);
        spansAccumulator.add(thirdSpan, firstScope, secondResource);
        spansAccumulator.add(forthSpan, secondScope, secondResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                spansAccumulator.getExportTraceServiceRequest();

        assertEquals(2, exportTraceServiceRequest.getResourceSpansList().size());
        assertTrue(findResource(exportTraceServiceRequest, firstResource));
        assertTrue(findResource(exportTraceServiceRequest, secondResource));
        assertEquals(2, countResources(exportTraceServiceRequest));


        assertTrue(findScope(exportTraceServiceRequest, firstResource, firstScope));
        assertTrue(findScope(exportTraceServiceRequest, firstResource, secondScope));
        assertTrue(findScope(exportTraceServiceRequest, secondResource, firstScope));
        assertTrue(findScope(exportTraceServiceRequest, secondResource, secondScope));
        assertEquals(2, countScopes(exportTraceServiceRequest, firstResource));
        assertEquals(2, countScopes(exportTraceServiceRequest, secondResource));

        assertTrue(findSpan(exportTraceServiceRequest, firstResource, firstScope, firstSpan));
        assertTrue(findSpan(exportTraceServiceRequest, firstResource, secondScope, secondSpan));
        assertTrue(findSpan(exportTraceServiceRequest, secondResource, firstScope, thirdSpan));
        assertTrue(findSpan(exportTraceServiceRequest, secondResource, secondScope, forthSpan));

        assertEquals(1,countSpans(exportTraceServiceRequest, firstResource, firstScope));
        assertEquals(1,countSpans(exportTraceServiceRequest, firstResource, secondScope));
        assertEquals(1,countSpans(exportTraceServiceRequest, secondResource, firstScope));
        assertEquals(1, countSpans(exportTraceServiceRequest, secondResource, secondScope));

    }

    private boolean findResource(ExportTraceServiceRequest exportTraceServiceRequest, Resource resource) {
        return exportTraceServiceRequest.getResourceSpansList().stream()
                .anyMatch(rs -> rs.getResource().equals(resource));
    }

    private int countResources(ExportTraceServiceRequest exportTraceServiceRequest) {
        return exportTraceServiceRequest.getResourceSpansList().size();
    }

    private boolean findScope(ExportTraceServiceRequest exportTraceServiceRequest, Resource resource, InstrumentationScope scope) {
        return exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .anyMatch(rs -> rs.getScopeSpansList().stream()
                        .anyMatch(is -> is.getScope().equals(scope)));
    }

    private int countScopes(ExportTraceServiceRequest exportTraceServiceRequest, Resource resource) {
        return exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .mapToInt(rs -> rs.getScopeSpansList().size())
                .sum();
    }

    private boolean findSpan(ExportTraceServiceRequest exportTraceServiceRequest, Resource resource,
                              InstrumentationScope scope, Span span) {
        return exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .anyMatch(rs -> rs.getScopeSpansList().stream()
                        .filter(is -> is.getScope().equals(scope))
                        .anyMatch(ss -> ss.getSpansList().contains(span)));
    }

    private long countSpans(ExportTraceServiceRequest exportTraceServiceRequest, Resource resource,
                         InstrumentationScope scope) {
        return exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(resource))
                .flatMap(rs -> rs.getScopeSpansList().stream())
                .filter(is -> is.getScope().equals(scope))
                .mapToInt(is -> is.getSpansList().size())
                .sum();
    }
}
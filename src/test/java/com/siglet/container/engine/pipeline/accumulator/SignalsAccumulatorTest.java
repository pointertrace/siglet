package com.siglet.container.engine.pipeline.accumulator;

import com.siglet.container.adapter.AdapterUtils;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.resource.v1.Resource;
import io.opentelemetry.proto.trace.v1.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignalsAccumulatorTest {


    private SignalsAccumulator signalsAccumulator;

    private Span firstSpan;

    private Span secondSpan;

    private Span thirdSpan;

    private Span forthSpan;

    private InstrumentationScope firstScope;

    private InstrumentationScope secondScope;

    private Resource firstResource;

    private Resource secondResource;


    @BeforeEach
    void setUp() throws Exception {

        signalsAccumulator = new SignalsAccumulator();

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

        signalsAccumulator.add(firstSpan, firstScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                signalsAccumulator.getExportTraceServiceRequest();

        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());
        // first resource
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"));


        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().getFirst().getScopeSpansList().size());
        // first resource - first scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));

        assertEquals(1,
                exportTraceServiceRequest.getResourceSpansList().getFirst()
                        .getScopeSpansList().getFirst().getSpansList().size());
        // first resource - first scope - first span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(firstSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));
    }


    @Test
    void twoSpanOneScopeOneResource() {

        signalsAccumulator.add(firstSpan, firstScope, firstResource);
        signalsAccumulator.add(secondSpan, firstScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                signalsAccumulator.getExportTraceServiceRequest();


        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());
        // first resource
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"));


        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().getFirst().getScopeSpansList().size());
        // first resource - first scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));

        assertEquals(2,
                exportTraceServiceRequest.getResourceSpansList().getFirst()
                        .getScopeSpansList().getFirst().getSpansList().size());
        // first resource - first scope - first span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(firstSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));

        // first resource - first scope - second span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(secondSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-span not found"));
    }

    @Test
    void twoSpanTwoScopeOneResource() {

        signalsAccumulator.add(firstSpan, firstScope, firstResource);
        signalsAccumulator.add(secondSpan, firstScope, firstResource);
        signalsAccumulator.add(thirdSpan, secondScope, firstResource);
        signalsAccumulator.add(forthSpan, secondScope, firstResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                signalsAccumulator.getExportTraceServiceRequest();

        assertEquals(1, exportTraceServiceRequest.getResourceSpansList().size());
        // first resource
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"));


        assertEquals(2, exportTraceServiceRequest.getResourceSpansList().getFirst().getScopeSpansList().size());
        // first resource - first scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));

        assertEquals(2,
                exportTraceServiceRequest.getResourceSpansList().getFirst()
                        .getScopeSpansList().getFirst().getSpansList().size());
        // first resource - first scope - first span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(firstSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-span not found"));

        // first resource - first scope - second span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(secondSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-span not found"));

        // first resource - second scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-second not found"));

        assertEquals(2,
                exportTraceServiceRequest.getResourceSpansList().getFirst()
                        .getScopeSpansList().get(1).getSpansList().size());
        // first resource - second scope - third span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(thirdSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("third-span not found"));

        // first resource - second scope - forth span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(s -> s.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"))
                .getSpansList().stream()
                .filter(s -> s.equals(forthSpan))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("forth-span not found"));
    }

    @Test
    void twoSpanTwoScopeTwoResource() {

        signalsAccumulator.add(firstSpan, firstScope, firstResource);
        signalsAccumulator.add(secondSpan, secondScope, firstResource);
        signalsAccumulator.add(thirdSpan, firstScope, secondResource);
        signalsAccumulator.add(forthSpan, secondScope, secondResource);

        ExportTraceServiceRequest exportTraceServiceRequest =
                signalsAccumulator.getExportTraceServiceRequest();

        // first resource
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"));

        // second resource
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(secondResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"));


        // first resource - first scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"));

        // first resource - second scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"));


        // second resource - first scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(secondResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"));

        // second resource - second scope
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(secondResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"));

        // first resource - first scope - first span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(sp -> sp.getName().equals("first-span"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first span not found"));

        // first resource - second scope - second span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(firstResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"))
                .getSpansList().stream()
                .filter(sp -> sp.getName().equals("second-span"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second span not found"));

        // second resource - first scope - third span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(secondResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(firstScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("first-scope not found"))
                .getSpansList().stream()
                .filter(sp -> sp.getName().equals("third-span"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("third span not found"));

        // second resource - second scope - forth span
        exportTraceServiceRequest.getResourceSpansList().stream()
                .filter(rs -> rs.getResource().equals(secondResource))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-resource not found"))
                .getScopeSpansList().stream()
                .filter(ss -> ss.getScope().equals(secondScope))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("second-scope not found"))
                .getSpansList().stream()
                .filter(sp -> sp.getName().equals("forth-span"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("forth span not found"));

    }
}
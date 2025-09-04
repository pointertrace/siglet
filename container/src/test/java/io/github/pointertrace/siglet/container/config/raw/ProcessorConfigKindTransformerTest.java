package io.github.pointertrace.siglet.container.config.raw;

import io.github.pointertrace.siglet.parser.ValueTransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ProcessorConfigKindTransformerTest {

    private ProcessorKindTransformer processorKindTransformer;

    @BeforeEach
    public void setUp() {
        processorKindTransformer = new ProcessorKindTransformer();
    }

    @Test
    void transform() throws Exception {

        assertEquals(ProcessorKind.METRICLET, processorKindTransformer.transform("metriclet"));

        assertEquals(ProcessorKind.SPANLET, processorKindTransformer.transform("spanlet"));

        assertEquals(ProcessorKind.TRACELET, processorKindTransformer.transform("tracelet"));

        assertEquals(ProcessorKind.TRACE_AGGREGATOR, processorKindTransformer.transform("trace-aggregator"));

    }

    @Test
    void transform_invalidValue(){

        ValueTransformerException e = assertThrowsExactly(ValueTransformerException.class,() -> processorKindTransformer.transform(
                "invalid"));

        assertEquals("The value [invalid] is not a valid sigletClass kind [spanlet, tracelet, " +
                "trace-aggregator, metriclet]", e.getMessage());

    }

}
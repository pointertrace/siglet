package com.siglet.config.item;

import com.siglet.SigletError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SigletKindTransformerTest {

    private ProcessorKindTransformer processorKindTransformer;

    @BeforeEach
    public void setUp() {
        processorKindTransformer = new ProcessorKindTransformer();
    }

    @Test
    void transform() {

        assertEquals(SigletKind.METRICLET,processorKindTransformer.transform("metriclet"));

        assertEquals(SigletKind.SPANLET,processorKindTransformer.transform("spanlet"));

        assertEquals(SigletKind.TRACELET,processorKindTransformer.transform("tracelet"));

        assertEquals(SigletKind.TRACE_AGGREGATOR,processorKindTransformer.transform("trace-aggregator"));

    }

    @Test
    void transform_invalidValue(){

        SigletError e = assertThrowsExactly(SigletError.class,() -> processorKindTransformer.transform("invalid"));

        assertEquals("The value [invalid] is not a valid processor kind [spanlet, tracelet, trace-aggregator, metriclet]", e.getMessage());

    }

}
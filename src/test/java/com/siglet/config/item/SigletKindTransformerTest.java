package com.siglet.config.item;

import com.siglet.SigletError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SigletKindTransformerTest {

    private SigletKindTransformer sigletKindTransformer;

    @BeforeEach
    public void setUp() {
        sigletKindTransformer = new SigletKindTransformer();
    }

    @Test
    void transform() {

        assertEquals(SigletKind.METRICLET, sigletKindTransformer.transform("metriclet"));

        assertEquals(SigletKind.SPANLET, sigletKindTransformer.transform("spanlet"));

        assertEquals(SigletKind.TRACELET, sigletKindTransformer.transform("tracelet"));

        assertEquals(SigletKind.TRACE_AGGREGATOR, sigletKindTransformer.transform("trace-aggregator"));

    }

    @Test
    void transform_invalidValue(){

        SigletError e = assertThrowsExactly(SigletError.class,() -> sigletKindTransformer.transform("invalid"));

        assertEquals("The value [invalid] is not a valid siglet kind [spanlet, tracelet, " +
                "trace-aggregator, metriclet]", e.getMessage());

    }

}
package com.siglet.container.config.raw;

import com.siglet.parser.ValueTransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class SignalTransformerTest {

    SignalTransformer signalTransformer;

    @BeforeEach
    void setUp() {
        signalTransformer = new SignalTransformer();
    }

    @Test
    void transform_nonStringValue() {
        ValueTransformerException e = assertThrowsExactly(ValueTransformerException.class,
                () -> signalTransformer.transform(25));
        assertEquals("The value is of type java.lang.Integer and it should be a string!", e.getMessage());
    }

    @Test
    void transform_nonValidEnumValue() {
        ValueTransformerException e = assertThrowsExactly(ValueTransformerException.class,
                () -> signalTransformer.transform("invalid-value"));
        assertEquals("The value [invalid-value] is not a valid signal type [trace, metric]!", e.getMessage());
    }

    @Test
    void transform_internalVLue() {
        ValueTransformerException e = assertThrowsExactly(ValueTransformerException.class,
                () -> signalTransformer.transform("SIGNAL"));
        assertEquals("SignalType value SIGNAL cannot be used in configuration!", e.getMessage());
    }

    @Test
    void transform() throws ValueTransformerException {
        assertEquals(SignalType.TRACE,signalTransformer.transform("TRACE"));
    }
}
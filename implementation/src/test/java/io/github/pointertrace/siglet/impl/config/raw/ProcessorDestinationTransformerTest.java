package io.github.pointertrace.siglet.impl.config.raw;

import io.github.pointertrace.siglet.parser.ValueTransformerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessorDestinationTransformerTest {

    ProcessorDestinationTransformer processorDestinationTransformer;

    @BeforeEach
    void setUp() {
        processorDestinationTransformer = new ProcessorDestinationTransformer();
    }

    @Test
    void transform_null() {

        ValueTransformerException e = assertThrows(ValueTransformerException.class,
                () -> processorDestinationTransformer.transform(null));

        assertEquals("The value is null!", e.getMessage());

    }

    @Test
    void transform_notString() {

        ValueTransformerException e = assertThrows(ValueTransformerException.class,
                () -> processorDestinationTransformer.transform(1));

        assertEquals("The value is of type java.lang.Integer and it should be a string!", e.getMessage());

    }

    @Test
    void transform_notInFormat() {

        ValueTransformerException e = assertThrows(ValueTransformerException.class,
                () -> processorDestinationTransformer.transform("a:b:c"));

        assertEquals("The value of a destination must be <destination> or <alias>:<destination>!", e.getMessage());

    }

    @Test
    void transform() throws ValueTransformerException {

        LocatedString locatedString = (LocatedString) processorDestinationTransformer.transform("destination");

        assertEquals("destination", locatedString.getValue());

    }

    @Test
    void transform_withAlias() throws ValueTransformerException {

        LocatedString locatedString = (LocatedString) processorDestinationTransformer.transform("alias:destination");

        assertEquals("alias:destination", locatedString.getValue());

    }
}
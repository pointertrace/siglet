package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnyNumberCheckerTest {

    private String expected;

    @Test
    void describe() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker(new IntRangeChecker(1,180));

        expected = """
                number
                  int range  (from 1 to 180 inclusive)""";

        assertEquals(expected, anyNumberChecker.describe());

    }

    @Test
    void getValue() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker();

        ConfigParser parser = new ConfigParser();

        Node node = parser.parse("100");

        anyNumberChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        ValueItem<Number> anyNumber = assertInstanceOf(ValueItem.class, value);

        assertEquals(100, anyNumber.getValue());


    }

    @Test
    void check_invalid() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker();


        ConfigParser parser = new ConfigParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> anyNumberChecker.check(node));

        assertEquals("(1:1) is not a number value!", ex.explain());

    }
}
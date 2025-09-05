package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnyNumberCheckerTest {

    private String expected;

    @Test
    void describe() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker(new IntRangeChecker(1,180));

        expected = "number\n" +
                   "  int range  (from 1 to 180 inclusive)";

        assertEquals(expected, anyNumberChecker.describe());

    }

    @Test
    void getValue() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker();

        YamlParser parser = new YamlParser();

        Node node = parser.parse("100");

        anyNumberChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        Number anyNumber = assertInstanceOf(Number.class, value);

        assertEquals(100, anyNumber);


    }

    @Test
    void check_invalid() {

        AnyNumberChecker anyNumberChecker = new AnyNumberChecker();


        YamlParser parser = new YamlParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> anyNumberChecker.check(node));

        assertEquals("(1:1) is not a number value!", ex.getMessage());

    }
}
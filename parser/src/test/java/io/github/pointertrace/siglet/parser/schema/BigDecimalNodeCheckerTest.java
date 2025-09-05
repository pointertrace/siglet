package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalNodeCheckerTest {


    @Test
    void describe() {

        BigDecimalChecker bigDecimalChecker = new BigDecimalChecker();

        assertEquals("bigDecimal",bigDecimalChecker.describe());
    }

    @Test
    void getValue() {

        BigDecimalChecker  bigDecimalChecker = new BigDecimalChecker();

        YamlParser parser = new YamlParser();

        BigDecimal valueToBeParsed = BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.valueOf(Double.MAX_VALUE));

        Node node = parser.parse(valueToBeParsed.toString());

        bigDecimalChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        Number anyNumber = assertInstanceOf(Number.class, value);

        assertEquals(valueToBeParsed, anyNumber);

    }

    @Test
    void check_invalid() {

        BigDecimalChecker bigDecimalChecker = new BigDecimalChecker();


        YamlParser parser = new YamlParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> bigDecimalChecker.check(node));

        assertEquals("(1:1) is not a big decimal value!", ex.getMessage());

    }
}
package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigIntegerNodeCheckerTest {

    @Test
    void describe() {

        BigIntegerChecker bigInteger = new BigIntegerChecker();

        assertEquals("bigInteger",bigInteger.describe());
    }

    @Test
    void getValue() {

        BigIntegerChecker  bigIntegerChecker = new BigIntegerChecker();


        YamlParser parser = new YamlParser();

        BigInteger valueToBeParsed = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(Long.MAX_VALUE));

        Node node = parser.parse(valueToBeParsed.toString());

        bigIntegerChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        Number anyNumber = assertInstanceOf(Number.class, value);

        assertEquals(valueToBeParsed, anyNumber);

    }

    @Test
    void check_invalid() {

        BigIntegerChecker  bigIntegerChecker = new BigIntegerChecker();

        YamlParser parser = new YamlParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> bigIntegerChecker.check(node));

        assertEquals("(1:1) is not a big integer value!", ex.getMessage());

    }
}
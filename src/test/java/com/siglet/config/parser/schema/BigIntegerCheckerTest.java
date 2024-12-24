package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigIntegerCheckerTest {

    @Test
    void describe() {

        BigIntegerChecker  bigInteger = new BigIntegerChecker();

        assertEquals("bigInteger",bigInteger.describe());
    }

    @Test
    void getValue() {

        BigIntegerChecker  bigIntegerChecker = new BigIntegerChecker();


        ConfigParser parser = new ConfigParser();

        BigInteger valueToBeParsed = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(Long.MAX_VALUE));

        Node node = parser.parse(valueToBeParsed.toString());

        bigIntegerChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        ValueItem<Number> anyNumber = assertInstanceOf(ValueItem.class, value);

        assertEquals(valueToBeParsed, anyNumber.getValue());

    }

    @Test
    void check_invalid() {

        BigIntegerChecker  bigIntegerChecker = new BigIntegerChecker();

        ConfigParser parser = new ConfigParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> bigIntegerChecker.check(node));

        assertEquals("(1:1) is not a big integer value!", ex.explain());

    }
}
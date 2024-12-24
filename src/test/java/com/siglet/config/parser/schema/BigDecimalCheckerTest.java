package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalCheckerTest {


    @Test
    void describe() {

        BigDecimalChecker  bigDecimalChecker = new BigDecimalChecker();

        assertEquals("bigDecimal",bigDecimalChecker.describe());
    }

    @Test
    void getValue() {

        BigDecimalChecker  bigDecimalChecker = new BigDecimalChecker();

        ConfigParser parser = new ConfigParser();

        BigDecimal valueToBeParsed = BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.valueOf(Double.MAX_VALUE));

        Node node = parser.parse(valueToBeParsed.toString());

        bigDecimalChecker.check(node);

        Object value = node.getValue();

        assertNotNull(value);
        ValueItem<Number> anyNumber = assertInstanceOf(ValueItem.class, value);

        assertEquals(valueToBeParsed, anyNumber.getValue());

    }

    @Test
    void check_invalid() {

        BigDecimalChecker bigDecimalChecker = new BigDecimalChecker();


        ConfigParser parser = new ConfigParser();

        Node node = parser.parse("text");

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,
                () -> bigDecimalChecker.check(node));

        assertEquals("(1:1) is not a big decimal value!", ex.explain());

    }
}
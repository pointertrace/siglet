package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlternativeCheckerTest {

    private AlternativeChecker alternativeChecker;

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    public void check_firstOption() throws SchemaValidationException {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        ConfigNode node = parser.parse("""
                text value
                """);

        alternativeChecker.check(node);

        String text = assertInstanceOf(String.class, node.getValue());
        assertEquals("text value", text);
    }


    @Test
    public void check_secondOption() throws SchemaValidationException {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        ConfigNode node = parser.parse("""
                - first value
                - second value
                """);

        alternativeChecker.check(node);

        List<?> list = assertInstanceOf(List.class, node.getValue());
        assertEquals(2, list.size());
        assertEquals("first value", list.get(0));
        assertEquals("second value", list.get(1));
    }

    @Test
    public void check_noValidOption() throws SchemaValidationException {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        ConfigNode node = parser.parse("""
                field: field value
                """);

      var ex = assertThrows(SchemaValidationException.class, () ->  alternativeChecker.check(node));
      assertEquals("""
              None of alternatives are valid:
                - text because: (1:1) is not a text value!
                - array because: (1:1) is not a array!   
              """, ex.getMessage());

    }
}


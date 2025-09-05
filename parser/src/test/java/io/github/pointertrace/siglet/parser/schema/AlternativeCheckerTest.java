package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlternativeCheckerTest {

    private AlternativeChecker alternativeChecker;

    private YamlParser parser;

    private String expected;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        expected = "alternative\n" +
                   "  text\n" +
                   "  array\n" +
                   "    text";

        assertEquals(expected, alternativeChecker.describe());
    }

    @Test
    void check_firstOption() throws SchemaValidationError {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

       Node node = parser.parse("text value\n");

        alternativeChecker.check(node);

        String textNode = assertInstanceOf(String.class, node.getValue());
        assertEquals("text value", textNode);
    }


    @Test
    void check_secondOption() throws SchemaValidationError {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        Node node = parser.parse("- first value\n" +
                                 "- second value\n");

        alternativeChecker.check(node);

        List<String> list = assertInstanceOf(List.class, node.getValue());
        assertEquals(2, list.size());
        assertEquals("first value", list.get(0));
        assertEquals("second value", list.get(1));
    }

    @Test
    void check_noValidOption() throws SchemaValidationError {

        alternativeChecker = new AlternativeChecker(
                new TextChecker(),
                new ArrayChecker(new TextChecker())
        );

        Node node = parser.parse("field: field value\n");

        SchemaValidationError ex = assertThrows(SchemaValidationError.class, () -> alternativeChecker.check(node));

        expected = "(1:1) None of alternatives are valid because:\n" +
                   "  (1:1) is not a text value!\n" +
                   "  (1:1) is not a array!";

        assertEquals(expected, ex.getMessage());

    }
}


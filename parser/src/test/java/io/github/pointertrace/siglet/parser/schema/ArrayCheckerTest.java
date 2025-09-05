package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayCheckerTest {

    private ArrayChecker arrayChecker;

    private YamlParser parser;

    private String expected;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() throws SchemaValidationError {


        arrayChecker = new ArrayChecker(new IntChecker());

        expected = "array\n" +
                   "  int";

        assertEquals(expected, arrayChecker.describe());


    }

    @Test
    void array() throws SchemaValidationError {

        Node node = parser.parse("- 1\n" +
                                 "- 2\n");

        arrayChecker = new ArrayChecker(new IntChecker());

        arrayChecker.check(node);

    }


    @Test
    void array_invalid() throws SchemaValidationError {

        Node node = parser.parse("key: value\n");

        arrayChecker = new ArrayChecker(new IntChecker());

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        assertEquals("(1:1) is not a array!", ex.getMessage());

    }

    @Test
    void array_itemInvalidSingleValidationError() throws SchemaValidationError {

        Node node = parser.parse("- str valuer\n");

        arrayChecker = new ArrayChecker(new IntChecker());

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        expected = "(1:3) array item is not valid because:\n" +
                   "  (1:3) is not a int value!";

        assertEquals(expected, ex.getMessage());

    }

    @Test
    void array_itemInvalidMultipleValidationError() throws SchemaValidationError {

        Node node = parser.parse("- key: value\n");

        arrayChecker = new ArrayChecker(new AlternativeChecker(new TextChecker(), new IntChecker()));

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        expected = "(1:1) array item is not valid because:\n" +
                   "  (1:3) None of alternatives are valid because:\n" +
                   "    (1:3) is not a text value!\n" +
                   "    (1:3) is not a int value!";

        assertEquals(expected, ex.getMessage());

    }
}
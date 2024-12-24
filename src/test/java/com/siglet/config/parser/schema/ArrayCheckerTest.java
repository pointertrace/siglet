package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayCheckerTest {

    private ArrayChecker arrayChecker;

    private ConfigParser parser;

    private String expected;

    @BeforeEach
    void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void describe() throws SchemaValidationError {


        arrayChecker = new ArrayChecker(new IntChecker());

        expected = """
                array
                  int""";


        assertEquals(expected, arrayChecker.describe());


    }
    @Test
    void array() throws SchemaValidationError {

        Node node = parser.parse("""
                - 1
                - 2
                """);

        arrayChecker = new ArrayChecker(new IntChecker());

        arrayChecker.check(node);


    }


    @Test
    void array_invalid() throws SchemaValidationError {

        Node node = parser.parse("""
                key: value
                """);

        arrayChecker = new ArrayChecker(new IntChecker());

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        assertEquals("(1:1) is not a array!", ex.explain());

    }

    @Test
    void array_itemInvalidSingleValidationError() throws SchemaValidationError {

        Node node = parser.parse("""
                - str valuer
                """);

        arrayChecker = new ArrayChecker(new IntChecker());

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        expected = """
                (1:3) array item is not valid because:
                  (1:3) is not a int value!""";

        assertEquals(expected, ex.explain());

    }

    @Test
    void array_itemInvalidMultipleValidationError() throws SchemaValidationError {

        Node node = parser.parse("""
                - key: value
                """);

        arrayChecker = new ArrayChecker(new AlternativeChecker(new TextChecker(), new IntChecker()));

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class, () -> {
            arrayChecker.check(node);
        });

        expected = """
                (1:1) array item is not valid because:
                  (1:3) None of alternatives are valid because:
                    (1:3) is not a text value!
                    (1:3) is not a int value!""";

        assertEquals(expected, ex.explain());

    }
}
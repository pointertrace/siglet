package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DomainCheckerTest {

    private String expected;

    @Test
    void describe() {

        TextChecker checker = new TextChecker(new DomainChecker(() -> Arrays.asList("a","b","c")));

        expected = "text\n" +
                   "  domain  (values [a, b, c])";

        assertEquals(expected, checker.describe());
    }

    @Test
    void check() {

        TextChecker checker = new TextChecker(new DomainChecker(() -> Arrays.asList("a","b","c")));

        String yaml = "a";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        checker.check(root);

        String value = assertInstanceOf(String.class, root.getValue());

        assertEquals("a", value);

    }

    @Test
    void check_notInSet() {
        TextChecker checker = new TextChecker(new DomainChecker(() -> Arrays.asList("a","b","c")));


        String yaml = "d";

        YamlParser parser = new YamlParser();

        Node root = parser.parse(yaml);

        SingleSchemaValidationError ex = assertThrowsExactly(SingleSchemaValidationError.class,() -> { checker.check(root); });

        assertEquals("(1:1) must be one of [a, b, c]!", ex.getMessage());

    }
}
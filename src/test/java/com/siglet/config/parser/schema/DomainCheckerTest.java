package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainCheckerTest {

    private String expected;

    @Test
    void describe() {

        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));

        expected = """
                text
                  domain  (values [a, b, c])""";

        assertEquals(expected, checker.describe());
    }

    @Test
    void check() {

        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));

        var yaml = "a";

        ConfigParser parser = new ConfigParser();

        Node root = parser.parse(yaml);

        checker.check(root);

        String value = assertInstanceOf(String.class, root.getValue());

        assertEquals("a", value);

    }

    @Test
    void check_notInSet() {
        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));


        var yaml = "d";

        ConfigParser parser = new ConfigParser();

        Node root = parser.parse(yaml);

        var ex = assertThrowsExactly(SingleSchemaValidationError.class,() -> { checker.check(root); });

        assertEquals("(1:1) must be one of [a, b, c]!", ex.getMessage());

    }
}
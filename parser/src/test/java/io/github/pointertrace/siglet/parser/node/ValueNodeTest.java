package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ValueNodeTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void parse() {

        Node node = parser.parse("string value");

        ValueNode.TextNode textNode = assertInstanceOf(ValueNode.TextNode.class, node);

        assertEquals(Location.of(1,1), textNode.getLocation());
        assertEquals("string value", textNode.getValue());

    }

    @Test
    void describe() {

        Node node = parser.parse("string value");

        assertEquals("(1:1)  text (string value)", node.describe(0));

    }

}
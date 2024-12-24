package com.siglet.config.parser.node;

import com.siglet.config.parser.ConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayNodeTest {
    private ConfigParser parser;

    private String expected;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void parse() {

        Node node = parser.parse("""
                - first item
                - 1""");

        ArrayNode objectConfigNode = assertInstanceOf(ArrayNode.class, node);

        assertEquals(2, objectConfigNode.getLength());

        ValueNode.Text text = assertInstanceOf(ValueNode.Text.class, objectConfigNode.getItem(0));
        assertEquals("first item", text.getValue().getValue());

        ValueNode.Int integer = assertInstanceOf(ValueNode.Int.class, objectConfigNode.getItem(1));
        assertEquals(1, integer.getValue().getValue());
    }

    @Test
    void describe() {

        Node node = parser.parse("""
                - first item
                - 1""");

        expected = """
                (1:1)  array
                  (1:3)  array item
                    (1:3)  text (first item)
                  (2:3)  array item
                    (2:3)  int (1)""";

        assertEquals(expected, node.describe());

    }

}
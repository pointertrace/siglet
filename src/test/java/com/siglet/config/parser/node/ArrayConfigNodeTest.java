package com.siglet.config.parser.node;

import com.siglet.config.parser.ConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayConfigNodeTest {
    private ConfigParser parser;

    private String expected;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void parse() {

        ConfigNode node = parser.parse("""
                - first item
                - 1""");

        ArrayConfigNode objectConfigNode = assertInstanceOf(ArrayConfigNode.class, node);

        assertEquals(2, objectConfigNode.getLength());

        ValueConfigNode.Text text = assertInstanceOf(ValueConfigNode.Text.class, objectConfigNode.getItem(0));
        assertEquals("first item", text.getValue().getValue());

        ValueConfigNode.Int integer = assertInstanceOf(ValueConfigNode.Int.class, objectConfigNode.getItem(1));
        assertEquals(1, integer.getValue().getValue());
    }

    @Test
    void describe() {

        ConfigNode node = parser.parse("""
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
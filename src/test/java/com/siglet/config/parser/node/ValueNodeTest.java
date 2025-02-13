package com.siglet.config.parser.node;

import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ValueNodeTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
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

        assertEquals("(1:1)  text (string value)", node.describe());

    }

}
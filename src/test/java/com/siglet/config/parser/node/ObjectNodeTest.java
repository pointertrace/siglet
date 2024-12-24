package com.siglet.config.parser.node;

import com.siglet.config.parser.ConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectNodeTest {

    private ConfigParser parser;

    private String expected;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void parse() {

        Node node = parser.parse("""
                key1: value1
                key2: value2""");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);

        assertEquals(2, objectConfigNode.getPropertyNames().size());
        assertTrue(objectConfigNode.getPropertyNames().contains("key1"));
        assertTrue(objectConfigNode.getPropertyNames().contains("key2"));

        ValueNode.Text textNode = assertInstanceOf(ValueNode.Text.class, objectConfigNode.get("key1"));
        assertEquals("value1", textNode.getValue().getValue());

        textNode = assertInstanceOf(ValueNode.Text.class, objectConfigNode.get("key2"));
        assertEquals("value2", textNode.getValue().getValue());
    }

    @Test
    void describe() {

        Node node = parser.parse("""
                key1: value1
                key2: value2""");

        expected = """
                (1:1)  object
                  (1:7)  property  (key1)
                    (1:7)  text (value1)
                  (2:7)  property  (key2)
                    (2:7)  text (value2)""";

        assertEquals(expected, node.describe());

    }
}
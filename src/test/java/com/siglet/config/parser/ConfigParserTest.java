package com.siglet.config.parser;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.ArrayNode;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.node.ObjectNode;
import com.siglet.config.parser.node.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigParserTest {

    private ConfigParser configParser;


    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
    }

    @Test
    void parseInt() {
        String config = "1";

        Node node = configParser.parse(config);

        var intNode = assertInstanceOf(ValueNode.IntNode.class, node);

        assertEquals(Location.of(1, 1), intNode.getLocation());
        assertEquals(1, intNode.getValue());

    }

    @Test
    void parseLong() {
        String config = "" + (Integer.MAX_VALUE + 1L);

        Node node = configParser.parse(config);

        var longNode = assertInstanceOf(ValueNode.LongNode.class, node);

        assertEquals(Location.of(1, 1), longNode.getLocation());
        assertEquals(Integer.MAX_VALUE + 1L, longNode.getValue());

    }

    @Test
    void parseBigInteger() {
        String config = ("" + Long.MAX_VALUE) + "0";

        Node node = configParser.parse(config);

        var bigIntegerNode = assertInstanceOf(ValueNode.BigIntegerNode.class, node);

        assertEquals(Location.of(1, 1), bigIntegerNode.getLocation());
        assertEquals(new BigInteger(("" + Long.MAX_VALUE) + "0"), bigIntegerNode.getValue());
    }

    @Test
    void parseFloat() {
        String config = "1.1";

        Node node = configParser.parse(config);

        var floatNode = assertInstanceOf(ValueNode.FloatNode.class, node);

        assertEquals(Location.of(1, 1), floatNode.getLocation());
        assertEquals(1.1f, floatNode.getValue());
    }

    @Test
    void parseDouble() {
        String config = "4.4E38";

        Node node = configParser.parse(config);

        var doubleNode = assertInstanceOf(ValueNode.DoubleNode.class, node);

        assertEquals(Location.of(1, 1), doubleNode.getLocation());
        assertEquals(Double.parseDouble("4.4E38"), doubleNode.getValue());
    }

    @Test
    void parseBigDecimal() {
        String config = "9.9E1000";

        Node node = configParser.parse(config);

        var bigDecimalNode = assertInstanceOf(ValueNode.BigDecimalNode.class, node);

        assertEquals(Location.of(1, 1), bigDecimalNode.getLocation());
        assertEquals(new BigDecimal("9.9E1000"), bigDecimalNode.getValue());
    }

    @Test
    void parseText() {
        String config = "text value";

        Node node = configParser.parse(config);

        var textNode = assertInstanceOf(ValueNode.TextNode.class, node);

        assertEquals(Location.of(1, 1), textNode.getLocation());
        assertEquals("text value", textNode.getValue());
    }

    @Test
    void parseBooleanTrue() {
        String config = "true";

        Node node = configParser.parse(config);

        var booleanNode = assertInstanceOf(ValueNode.BooleanNode.class, node);

        assertEquals(Location.of(1, 1), booleanNode.getLocation());
        assertEquals(Boolean.TRUE, booleanNode.getValue());
    }

    @Test
    void parseBooleanFalse() {
        String config = "false";

        Node node = configParser.parse(config);

        var booleanNode = assertInstanceOf(ValueNode.BooleanNode.class, node);

        assertEquals(Location.of(1, 1), booleanNode.getLocation());
        assertEquals(Boolean.FALSE, booleanNode.getValue());
    }

    @Test
    void parseNull() {
        String config = "null";

        Node node = configParser.parse(config);

        var nullNode= assertInstanceOf(ValueNode.NullNode.class, node);

        assertEquals(Location.of(1, 1), nullNode.getLocation());
        assertNull(nullNode.getValue());
    }

    @Test
    void parseObject() {
        String config = """
                first-object:
                    prop1: prop1-value
                    prop2:
                        prop2.1: prop2.1-value
                """;

        Node node = configParser.parse(config);

        var rootObject = assertInstanceOf(ObjectNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertEquals(Set.of("first-object"), rootObject.getPropertyNames());
        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        var firstObject = assertInstanceOf(ObjectNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(2, 5), firstObject.getPropertyKeyLocation("prop1"));
        assertEquals(Location.of(3, 5), firstObject.getPropertyKeyLocation("prop2"));

        var prop1 = assertInstanceOf(ValueNode.TextNode.class,firstObject.get("prop1"));
        assertEquals(Location.of(2, 12), prop1.getLocation());
        assertEquals("prop1-value",prop1.getValue());


        var prop2 = assertInstanceOf(ObjectNode.class,firstObject.get("prop2"));
        assertEquals(Location.of(3,5), prop2.getLocation());


        assertEquals(Location.of(4, 9), prop2.getPropertyKeyLocation("prop2.1"));

        var prop21 = assertInstanceOf(ValueNode.class,prop2.get("prop2.1"));

        assertEquals("prop2.1-value",prop21.getValue());

    }

    @Test
    void parseArray() {
        String config = """
                first-object:
                    - item1
                    - item2
                    - item3:
                      - item3.1
                      - item3.2
                      - item3.3
                """;

        Node node = configParser.parse(config);

        var rootObject = assertInstanceOf(ObjectNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertEquals(Set.of("first-object"), rootObject.getPropertyNames());
        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        var array  = assertInstanceOf(ArrayNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(1, 1), array.getLocation());
        assertEquals(3, array.getLength());

        var item1 = assertInstanceOf(ValueNode.class,array.getItem(0));
        assertEquals(Location.of(2,7),item1.getLocation());
        assertEquals("item1",item1.getValue());

        var item2 = assertInstanceOf(ValueNode.class,array.getItem(1));
        assertEquals(Location.of(3,7),item2.getLocation());
        assertEquals("item2",item2.getValue());

        var item3 = assertInstanceOf(ObjectNode.class,array.getItem(2));
        assertEquals(Location.of(4,7),item3.getLocation());

        assertEquals(1, item3.getProperties().size());
        assertEquals(Set.of("item3"), item3.getPropertyNames());

        var item3Array  = assertInstanceOf(ArrayNode.class, item3.get("item3"));

        assertEquals(Location.of(4, 7), item3Array.getLocation());
        assertEquals(3, item3Array.getLength());

        var item31 = assertInstanceOf(ValueNode.class, item3Array.getItem(0));
        assertEquals(Location.of(5, 9), item31.getLocation());
        assertEquals("item3.1", item31.getValue());

        var item32 = assertInstanceOf(ValueNode.class, item3Array.getItem(1));
        assertEquals(Location.of(6, 9), item32.getLocation());
        assertEquals("item3.2", item32.getValue());

        var item33 = assertInstanceOf(ValueNode.class, item3Array.getItem(2));
        assertEquals(Location.of(7, 9), item33.getLocation());
        assertEquals("item3.3", item33.getValue());

    }

}
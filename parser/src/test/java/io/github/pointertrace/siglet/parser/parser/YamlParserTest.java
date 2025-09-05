package io.github.pointertrace.siglet.parser.parser;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.node.ArrayNode;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import io.github.pointertrace.siglet.parser.node.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class YamlParserTest {

    private YamlParser yamlParser;


    @BeforeEach
    void setUp() {
        yamlParser = new YamlParser();
    }

    @Test
    void parseInt() {
        String config = "1";

        Node node = yamlParser.parse(config);

        ValueNode intNode = assertInstanceOf(ValueNode.IntNode.class, node);

        assertEquals(Location.of(1, 1), intNode.getLocation());
        assertEquals(1, intNode.getValue());

    }

    @Test
    void parseLong() {
        String config = "" + (Integer.MAX_VALUE + 1L);

        Node node = yamlParser.parse(config);

        ValueNode longNode = assertInstanceOf(ValueNode.LongNode.class, node);

        assertEquals(Location.of(1, 1), longNode.getLocation());
        assertEquals(Integer.MAX_VALUE + 1L, longNode.getValue());

    }

    @Test
    void parseBigInteger() {
        String config = ("" + Long.MAX_VALUE) + "0";

        Node node = yamlParser.parse(config);

        ValueNode bigIntegerNode = assertInstanceOf(ValueNode.BigIntegerNode.class, node);

        assertEquals(Location.of(1, 1), bigIntegerNode.getLocation());
        assertEquals(new BigInteger(("" + Long.MAX_VALUE) + "0"), bigIntegerNode.getValue());
    }

    @Test
    void parseFloat() {
        String config = "1.1";

        Node node = yamlParser.parse(config);

        ValueNode floatNode = assertInstanceOf(ValueNode.FloatNode.class, node);

        assertEquals(Location.of(1, 1), floatNode.getLocation());
        assertEquals(1.1f, floatNode.getValue());
    }

    @Test
    void parseDouble() {
        String config = "4.4E38";

        Node node = yamlParser.parse(config);

        ValueNode doubleNode = assertInstanceOf(ValueNode.DoubleNode.class, node);

        assertEquals(Location.of(1, 1), doubleNode.getLocation());
        assertEquals(Double.parseDouble("4.4E38"), doubleNode.getValue());
    }

    @Test
    void parseBigDecimal() {
        String config = "9.9E1000";

        Node node = yamlParser.parse(config);

        ValueNode bigDecimalNode = assertInstanceOf(ValueNode.BigDecimalNode.class, node);

        assertEquals(Location.of(1, 1), bigDecimalNode.getLocation());
        assertEquals(new BigDecimal("9.9E1000"), bigDecimalNode.getValue());
    }

    @Test
    void parseText() {
        String config = "text value";

        Node node = yamlParser.parse(config);

        ValueNode textNode = assertInstanceOf(ValueNode.TextNode.class, node);

        assertEquals(Location.of(1, 1), textNode.getLocation());
        assertEquals("text value", textNode.getValue());
    }

    @Test
    void parseBooleanTrue() {
        String config = "true";

        Node node = yamlParser.parse(config);

        ValueNode booleanNode = assertInstanceOf(ValueNode.BooleanNode.class, node);

        assertEquals(Location.of(1, 1), booleanNode.getLocation());
        assertEquals(Boolean.TRUE, booleanNode.getValue());
    }

    @Test
    void parseBooleanFalse() {
        String config = "false";

        Node node = yamlParser.parse(config);

        ValueNode booleanNode = assertInstanceOf(ValueNode.BooleanNode.class, node);

        assertEquals(Location.of(1, 1), booleanNode.getLocation());
        assertEquals(Boolean.FALSE, booleanNode.getValue());
    }

    @Test
    void parseNull() {
        String config = "null";

        Node node = yamlParser.parse(config);

        ValueNode nullNode= assertInstanceOf(ValueNode.NullNode.class, node);

        assertEquals(Location.of(1, 1), nullNode.getLocation());
        assertNull(nullNode.getValue());
    }

    @Test
    void parseObject() {
        String config = "first-object:\n" +
                        "    prop1: prop1-value\n" +
                        "    prop2:\n" +
                        "        prop2.1: prop2.1-value\n";

        Node node = yamlParser.parse(config);

        ObjectNode rootObject = assertInstanceOf(ObjectNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertArrayEquals(new Object[] {"first-object"}, rootObject.getPropertyNames().toArray());
        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        ObjectNode firstObject = assertInstanceOf(ObjectNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(2, 5), firstObject.getPropertyKeyLocation("prop1"));
        assertEquals(Location.of(3, 5), firstObject.getPropertyKeyLocation("prop2"));

        ValueNode.TextNode prop1 = assertInstanceOf(ValueNode.TextNode.class,firstObject.get("prop1"));
        assertEquals(Location.of(2, 12), prop1.getLocation());
        assertEquals("prop1-value",prop1.getValue());


        ObjectNode prop2 = assertInstanceOf(ObjectNode.class,firstObject.get("prop2"));
        assertEquals(Location.of(3,5), prop2.getLocation());


        assertEquals(Location.of(4, 9), prop2.getPropertyKeyLocation("prop2.1"));

        ValueNode prop21 = assertInstanceOf(ValueNode.class,prop2.get("prop2.1"));

        assertEquals("prop2.1-value",prop21.getValue());

    }

    @Test
    void parseArray() {
        String config = "first-object:\n" +
                        "    - item1\n" +
                        "    - item2\n" +
                        "    - item3:\n" +
                        "      - item3.1\n" +
                        "      - item3.2\n" +
                        "      - item3.3\n";

        Node node = yamlParser.parse(config);

        ObjectNode rootObject = assertInstanceOf(ObjectNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertArrayEquals(new Object[] {"first-object"}, rootObject.getPropertyNames().toArray());

        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        ArrayNode array  = assertInstanceOf(ArrayNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(1, 1), array.getLocation());
        assertEquals(3, array.getLength());

        ValueNode item1 = assertInstanceOf(ValueNode.class,array.getItem(0));
        assertEquals(Location.of(2,7),item1.getLocation());
        assertEquals("item1",item1.getValue());

        ValueNode item2 = assertInstanceOf(ValueNode.class,array.getItem(1));
        assertEquals(Location.of(3,7),item2.getLocation());
        assertEquals("item2",item2.getValue());

        ObjectNode item3 = assertInstanceOf(ObjectNode.class,array.getItem(2));
        assertEquals(Location.of(4,7),item3.getLocation());

        assertEquals(1, item3.getProperties().size());
        assertArrayEquals(new Object[] {"item3"},item3.getPropertyNames().toArray());

        ArrayNode item3Array  = assertInstanceOf(ArrayNode.class, item3.get("item3"));

        assertEquals(Location.of(4, 7), item3Array.getLocation());
        assertEquals(3, item3Array.getLength());

        ValueNode item31 = assertInstanceOf(ValueNode.class, item3Array.getItem(0));
        assertEquals(Location.of(5, 9), item31.getLocation());
        assertEquals("item3.1", item31.getValue());

        ValueNode item32 = assertInstanceOf(ValueNode.class, item3Array.getItem(1));
        assertEquals(Location.of(6, 9), item32.getLocation());
        assertEquals("item3.2", item32.getValue());

        ValueNode item33 = assertInstanceOf(ValueNode.class, item3Array.getItem(2));
        assertEquals(Location.of(7, 9), item33.getLocation());
        assertEquals("item3.3", item33.getValue());

    }

}
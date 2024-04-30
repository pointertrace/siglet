package com.siglet.config.parser;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.node.ArrayConfigNode;
import com.siglet.config.parser.node.ConfigNode;
import com.siglet.config.parser.node.ObjectConfigNode;
import com.siglet.config.parser.node.ValueConfigNode;
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
    public void parseInt() {
        String config = "1";

        ConfigNode node = configParser.parse(config);

        var intNode = assertInstanceOf(ValueConfigNode.Int.class, node);

        assertEquals(Location.of(1, 1), intNode.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, intNode.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(1, valueItem.getValue());

    }

    @Test
    public void parseLong() {
        String config = "" + (Integer.MAX_VALUE + 1L);

        ConfigNode node = configParser.parse(config);

        var longNode = assertInstanceOf(ValueConfigNode.Long.class, node);

        assertEquals(Location.of(1, 1), longNode.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, longNode.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(Integer.MAX_VALUE + 1L, valueItem.getValue());

    }

    @Test
    public void parseBigInteger() {
        String config = ("" + Long.MAX_VALUE) + "0";

        ConfigNode node = configParser.parse(config);

        var bigInteger = assertInstanceOf(ValueConfigNode.BigInteger.class, node);

        assertEquals(Location.of(1, 1), bigInteger.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, bigInteger.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(new BigInteger(("" + Long.MAX_VALUE) + "0"), valueItem.getValue());
    }

    @Test
    public void parseFloat() {
        String config = "1.1";

        ConfigNode node = configParser.parse(config);

        var floatNode = assertInstanceOf(ValueConfigNode.Float.class, node);

        assertEquals(Location.of(1, 1), floatNode.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, floatNode.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(1.1f, valueItem.getValue());
    }

    @Test
    public void parseDouble() {
        String config = "4.4E38";

        ConfigNode node = configParser.parse(config);

        var doubleNode = assertInstanceOf(ValueConfigNode.Double.class, node);

        assertEquals(Location.of(1, 1), doubleNode.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, doubleNode.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(Double.parseDouble("4.4E38"), valueItem.getValue());
    }

    @Test
    public void parseBigDecimal() {
        String config = "9.9E1000";

        ConfigNode node = configParser.parse(config);

        var bigDecimal = assertInstanceOf(ValueConfigNode.BigDecimal.class, node);

        assertEquals(Location.of(1, 1), bigDecimal.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, bigDecimal.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(new BigDecimal("9.9E1000"), valueItem.getValue());
    }

    @Test
    public void parseText() {
        String config = "text value";

        ConfigNode node = configParser.parse(config);

        var text = assertInstanceOf(ValueConfigNode.Text.class, node);

        assertEquals(Location.of(1, 1), text.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, text.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals("text value", valueItem.getValue());
    }

    @Test
    public void parseBooleanTrue() {
        String config = "true";

        ConfigNode node = configParser.parse(config);

        var bool = assertInstanceOf(ValueConfigNode.Boolean.class, node);

        assertEquals(Location.of(1, 1), bool.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, bool.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(Boolean.TRUE, valueItem.getValue());
    }

    @Test
    public void parseBooleanFalse() {
        String config = "false";

        ConfigNode node = configParser.parse(config);

        var bool = assertInstanceOf(ValueConfigNode.Boolean.class, node);

        assertEquals(Location.of(1, 1), bool.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, bool.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertEquals(Boolean.FALSE, valueItem.getValue());
    }

    @Test
    public void parseNull() {
        String config = "null";

        ConfigNode node = configParser.parse(config);

        var nullValue = assertInstanceOf(ValueConfigNode.Null.class, node);

        assertEquals(Location.of(1, 1), nullValue.getLocation());

        var valueItem = assertInstanceOf(ValueItem.class, nullValue.getValue());
        assertEquals(Location.of(1, 1), valueItem.getLocation());
        assertNull(valueItem.getValue());
    }

    @Test
    public void parseObject() {
        String config = """
                first-object:
                    prop1: prop1-value
                    prop2:
                        prop2.1: prop2.1-value
                """;

        ConfigNode node = configParser.parse(config);

        var rootObject = assertInstanceOf(ObjectConfigNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertEquals(Set.of("first-object"), rootObject.getPropertyNames());
        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        var firstObject = assertInstanceOf(ObjectConfigNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(2, 5), firstObject.getPropertyKeyLocation("prop1"));
        assertEquals(Location.of(3, 5), firstObject.getPropertyKeyLocation("prop2"));

        var prop1 = assertInstanceOf(ValueConfigNode.Text.class,firstObject.get("prop1"));
        assertEquals(Location.of(2, 12), prop1.getLocation());
        assertEquals("prop1-value",prop1.getValue().getValue());


        var prop2 = assertInstanceOf(ObjectConfigNode.class,firstObject.get("prop2"));
        assertEquals(Location.of(3,5), prop2.getLocation());


        assertEquals(Location.of(4, 9), prop2.getPropertyKeyLocation("prop2.1"));

        var prop21 = assertInstanceOf(ValueConfigNode.class,prop2.get("prop2.1"));

        assertEquals("prop2.1-value",prop21.getValue().getValue());

    }

    @Test
    public void parseArray() {
        String config = """
                first-object:
                    - item1
                    - item2
                    - item3:
                      - item3.1
                      - item3.2
                      - item3.3
                """;

        ConfigNode node = configParser.parse(config);

        var rootObject = assertInstanceOf(ObjectConfigNode.class, node);

        assertEquals(Location.of(1, 1), rootObject.getLocation());
        assertEquals(1, rootObject.getPropertyNames().size());
        assertEquals(Set.of("first-object"), rootObject.getPropertyNames());
        assertEquals(Location.of(1,1), rootObject.getPropertyKeyLocation("first-object"));

        var array  = assertInstanceOf(ArrayConfigNode.class,rootObject.get("first-object"));
        assertEquals(Location.of(1, 1), array.getLocation());
        assertEquals(3, array.getLength());

        var item1 = assertInstanceOf(ValueConfigNode.class,array.getItem(0));
        assertEquals(Location.of(2,7),item1.getLocation());
        assertEquals("item1",item1.getValue().getValue());

        var item2 = assertInstanceOf(ValueConfigNode.class,array.getItem(1));
        assertEquals(Location.of(3,7),item2.getLocation());
        assertEquals("item2",item2.getValue().getValue());

        var item3 = assertInstanceOf(ObjectConfigNode.class,array.getItem(2));
        assertEquals(Location.of(4,7),item3.getLocation());

        assertEquals(1, item3.getProperties().size());
        assertEquals(Set.of("item3"), item3.getPropertyNames());

        var item3Array  = assertInstanceOf(ArrayConfigNode.class, item3.get("item3"));

        assertEquals(Location.of(4, 7), item3Array.getLocation());
        assertEquals(3, item3Array.getLength());

        var item31 = assertInstanceOf(ValueConfigNode.class, item3Array.getItem(0));
        assertEquals(Location.of(5, 9), item31.getLocation());
        assertEquals("item3.1", item31.getValue().getValue());

        var item32 = assertInstanceOf(ValueConfigNode.class, item3Array.getItem(1));
        assertEquals(Location.of(6, 9), item32.getLocation());
        assertEquals("item3.2", item32.getValue().getValue());

        var item33 = assertInstanceOf(ValueConfigNode.class, item3Array.getItem(2));
        assertEquals(Location.of(7, 9), item33.getLocation());
        assertEquals("item3.3", item33.getValue().getValue());

    }

}
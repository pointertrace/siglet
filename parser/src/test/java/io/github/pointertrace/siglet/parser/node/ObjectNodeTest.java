package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectNodeTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void parse() {

        Node node = parser.parse("key1: value1\n" +
                                 "key2: value2");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);

        assertEquals(2, objectConfigNode.getPropertyNames().size());
        assertTrue(objectConfigNode.getPropertyNames().contains("key1"));
        assertTrue(objectConfigNode.getPropertyNames().contains("key2"));

        ValueNode.TextNode textNode = assertInstanceOf(ValueNode.TextNode.class, objectConfigNode.get("key1"));
        assertEquals("value1", textNode.getValue());

        textNode = assertInstanceOf(ValueNode.TextNode.class, objectConfigNode.get("key2"));
        assertEquals("value2", textNode.getValue());
    }

    @Test
    void describe() {

        Node node = parser.parse("key1: value1\n" +
                                 "key2: value2");

        String expected = "(1:1)  object\n" +
                          "  (1:7)  property  (key1)\n" +
                          "    (1:7)  text (value1)\n" +
                          "  (2:7)  property  (key2)\n" +
                          "    (2:7)  text (value2)";

        assertEquals(expected, node.describe(0));

    }


    @Test
    void getValue() {


        Node node = parser.parse("key1: value1\n" +
                                 "key2: value2");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);
        objectConfigNode.setValueCreator(Value::new);

        BaseNode key1 = objectConfigNode.getProperties().get("key1");
        assertNotNull(key1);
        key1.setValueSetter(ValueSetter.of(Value::setKey1));

        BaseNode key2 = objectConfigNode.getProperties().get("key2");
        assertNotNull(key2);
        key2.setValueSetter(ValueSetter.of(Value::setKey2));

        Object value = objectConfigNode.getValue();

        assertNotNull(value);
        Value valueWithoutLocation = assertInstanceOf(Value.class, value);
        assertEquals("value1", valueWithoutLocation.getKey1());
        assertEquals("value2", valueWithoutLocation.getKey2());

    }

    @Test
    void getValue_locatedValue() {


        Node node = parser.parse("key1: value1\n" +
                                 "key2: value2");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);
        objectConfigNode.setValueCreator(LocatedValue::new);

        BaseNode key1 = objectConfigNode.getProperties().get("key1");
        assertNotNull(key1);
        key1.setValueSetter(ValueSetter.of(LocatedValue::setKey1));
        key1.setLocationSetter(LocationSetter.of(LocatedValue::setKey1Location));

        BaseNode key2 = objectConfigNode.getProperties().get("key2");
        assertNotNull(key2);
        key2.setValueSetter(ValueSetter.of(LocatedValue::setKey2));
        key2.setLocationSetter(LocationSetter.of(LocatedValue::setKey2Location));

        Object value = objectConfigNode.getValue();

        assertNotNull(value);
        LocatedValue locatedValue = assertInstanceOf(LocatedValue.class, value);
        assertEquals(Location.of(1,1), locatedValue.getLocation());

        assertEquals("value1", locatedValue.getKey1());
        assertEquals(Location.of(1,7), locatedValue.getKey1Location());

        assertEquals("value2", locatedValue.getKey2());
        assertEquals(Location.of(2,7), locatedValue.getKey2Location());

    }
    static class Value {

        private String key1;

        private String key2;

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

    }

    static class LocatedValue implements Located {

        private Location location;

        private String key1;

        private Location key1Location;

        private String key2;

        private Location key2Location;

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

        public Location getKey1Location() {
            return key1Location;
        }

        public void setKey1Location(Location key1Location) {
            this.key1Location = key1Location;
        }

        public Location getKey2Location() {
            return key2Location;
        }

        public void setKey2Location(Location key2Location) {
            this.key2Location = key2Location;
        }
    }
}
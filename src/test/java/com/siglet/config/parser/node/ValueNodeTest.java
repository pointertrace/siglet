package com.siglet.config.parser.node;

import com.siglet.config.located.Located;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueNodeTest {

    private ConfigParser parser;

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

        String expected = """
                (1:1)  object
                  (1:7)  property  (key1)
                    (1:7)  text (value1)
                  (2:7)  property  (key2)
                    (2:7)  text (value2)""";

        assertEquals(expected, node.describe());

    }


    @Test
    void getValue_valueWithoutLocation() {


        Node node = parser.parse("""
                key1: value1
                key2: value2""");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);
        objectConfigNode.setValueCreator(ValueWithoutLocation::new);

        Node key1 = objectConfigNode.getProperties().get("key1");
        assertNotNull(key1);
        key1.setValueSetter(ValueSetter.of(ValueWithoutLocation::setKey1));

        Node key2 = objectConfigNode.getProperties().get("key2");
        assertNotNull(key2);
        key2.setValueSetter(ValueSetter.of(ValueWithoutLocation::setKey2));

        Object value = objectConfigNode.getValue();

        assertNotNull(value);
        ValueWithoutLocation valueWithoutLocation = assertInstanceOf(ValueWithoutLocation.class, value);
        assertEquals("value1", valueWithoutLocation.getKey1());
        assertEquals("value2", valueWithoutLocation.getKey2());

    }

    @Test
    void getValue_valueWithLocation() {


        Node node = parser.parse("""
                key1: value1
                key2: value2""");

        ObjectNode objectConfigNode = assertInstanceOf(ObjectNode.class, node);
        objectConfigNode.setValueCreator(ValueWithLocation::new);

        Node key1 = objectConfigNode.getProperties().get("key1");
        assertNotNull(key1);
        key1.setValueSetter(ValueSetter.of(ValueWithLocation::setKey1));
        key1.setLocationSetter(LocationSetter.of(ValueWithLocation::setKey1Location));

        Node key2 = objectConfigNode.getProperties().get("key2");
        assertNotNull(key2);
        key2.setValueSetter(ValueSetter.of(ValueWithLocation::setKey2));
        key2.setLocationSetter(LocationSetter.of(ValueWithLocation::setKey2Location));

        Object value = objectConfigNode.getValue();

        assertNotNull(value);
        ValueWithLocation valueWithLocation = assertInstanceOf(ValueWithLocation.class, value);
        assertEquals(Location.of(1,1), valueWithLocation.getLocation());

        assertEquals("value1", valueWithLocation.getKey1());
        assertEquals(Location.of(1,7), valueWithLocation.getKey1Location());

        assertEquals("value2", valueWithLocation.getKey2());
        assertEquals(Location.of(2,7), valueWithLocation.getKey2Location());

    }
    static class ValueWithoutLocation {

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

    static class ValueWithLocation implements Located {

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
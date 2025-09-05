package io.github.pointertrace.siglet.parser.node;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.located.Located;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class ArrayNodeTest {
    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }

    @Test
    void parse() {

        Node node = parser.parse("- first item\n" +
                                 "- 1");

        ArrayNode arrayConfigNode = assertInstanceOf(ArrayNode.class, node);
        assertEquals(2, arrayConfigNode.getLength());

        ValueNode.TextNode textNode = assertInstanceOf(ValueNode.TextNode.class, arrayConfigNode.getItem(0));
        assertEquals("first item", textNode.getValue());

        ValueNode.IntNode integer = assertInstanceOf(ValueNode.IntNode.class, arrayConfigNode.getItem(1));
        assertEquals(1, integer.getValue());
    }

    @Test
    void describe() {

        Node node = parser.parse("- first item\n" +
                                 "- 1");

        String expected = "(1:1)  array\n" +
                          "  (1:3)  array item\n" +
                          "    (1:3)  text (first item)\n" +
                          "  (2:3)  array item\n" +
                          "    (2:3)  int (1)";

        assertEquals(expected, node.describe(0));

    }

    @Test
    void getValue_valueWithoutLocation() {

        Node node = parser.parse("- first item\n" +
                                 "- 1");

        ArrayNode arrayConfigNode = assertInstanceOf(ArrayNode.class, node);
        arrayConfigNode.setValueCreator(ValueCreator.of(ArrayList::new));

        arrayConfigNode.setValueSetter(ValueSetter.of((BiConsumer<List<Object>, Object>) List::add));
        assertEquals(2, arrayConfigNode.getLength());

        Object values = arrayConfigNode.getValue();
        List<Object> valuesList = assertInstanceOf(List.class, values);

        assertEquals(2, valuesList.size());
        assertEquals("first item", valuesList.get(0));
        assertEquals(1, valuesList.get(1));

    }

    @Test
    void getValue_valueWithLocation() {

        Node node = parser.parse("- first item\n" +
                                 "- second item");

        ArrayNode arrayConfigNode = assertInstanceOf(ArrayNode.class, node);
        arrayConfigNode.setArraycontainerValueCreator(ValueCreator.of(ArrayContainer::new));
        arrayConfigNode.setArrayContainerValueSetter(ValueSetter.of(ArrayContainer::add));
        arrayConfigNode.setArrayItemCreator(LocatedString::new);
        arrayConfigNode.setArrayItemValueSetter(ValueSetter.of(LocatedString::setValue));

        assertEquals(2, arrayConfigNode.getLength());

        Object values = arrayConfigNode.getValue();
        ArrayContainer valuesContainer = assertInstanceOf(ArrayContainer.class, values);

        assertEquals(2, valuesContainer.getLocatedStrings().size());
        assertEquals(Location.of(1,1), valuesContainer.getLocation());

        LocatedString item = valuesContainer.getLocatedStrings().get(0);
        assertNotNull(item);
        assertEquals("first item", item.getValue());
        assertEquals(Location.of(1,3), item.getLocation());

        item = valuesContainer.getLocatedStrings().get(1);
        assertNotNull(item);
        assertEquals("second item", item.getValue());
        assertEquals(Location.of(2,3), item.getLocation());
    }

    static class ArrayContainer implements Located {

        private Location location;

        private final List<LocatedString> locatedStrings = new ArrayList<>();


        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

        public void add(LocatedString locatedString) {
            locatedStrings.add(locatedString);
        }

        public List<LocatedString> getLocatedStrings() {
            return Collections.unmodifiableList(locatedStrings);
        }
    }

    static class LocatedString implements Located {

        private String value;

        private Location location;

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public void setLocation(Location location) {
            this.location = location;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
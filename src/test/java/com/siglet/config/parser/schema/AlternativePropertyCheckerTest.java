package com.siglet.config.parser.schema;

import com.siglet.config.item.ArrayItem;
import com.siglet.config.item.Item;
import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.parser.schema.SchemaFactory.array;
import static com.siglet.config.parser.schema.SchemaFactory.text;
import static org.junit.jupiter.api.Assertions.*;

class AlternativePropertyCheckerTest {
    private ObjectChecker objectCheck;

    private ConfigParser parser;

    private String expected;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }


    @Test
    void describe() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        expected = """
                object
                  alternative property
                    alternative
                      property  (name:prop1, required:true)
                        text
                      property  (name:prop1, required:true)
                        array
                          text""";

        assertEquals(expected, objectCheck.describe());
    }

    @Test
    void check_firstAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("""
                prop1: text-value
                """);

        objectCheck.check(node);

        Object value = node.getValue();
        assertNotNull(value);
        var bean = assertInstanceOf(Bean.class, value);
        assertEquals(List.of("text-value"), bean.getProp1().getValue().stream().map(ValueItem::getValue).toList());
    }

    @Test
    void check_secondAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("""
                prop1:
                - first-value
                - second-value
                """);

        objectCheck.check(node);

        Object value = assertInstanceOf(Bean.class, node.getValue());
        assertNotNull(value);
        var bean = assertInstanceOf(Bean.class, value);
        assertEquals(List.of("first-value", "second-value"),
                bean.getProp1().getValue().stream().map(ValueItem::getValue).toList());
    }

    @Test
    void check_noValidOption() throws SchemaValidationError {
        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("""
                prop1:
                - 10
                """);

        MultipleSchemaValidationError ex = assertThrowsExactly(MultipleSchemaValidationError.class,
                () -> objectCheck.check(node));

        expected = """
        (1:1) None of alternatives are valid because:
          (1:1) property prop1 is not valid because:
            (1:1) is not a text value!
          (1:1) property prop1 is not valid because:
            (2:3) array item is not valid because:
              (2:3) is not a text value!""";

        assertEquals(expected, ex.explain());

    }

    public static class Bean extends Item {

        private ArrayItem<ValueItem<String>> prop1;

        public void setProp1(ArrayItem<ValueItem<String>> prop1) {
            this.prop1 = prop1;
        }

        public void setProp1AsValue(ValueItem<String> prop1) {
            this.prop1 = new ArrayItem<>(Location.of(0, 0), List.of(prop1));
        }

        public ArrayItem<ValueItem<String>> getProp1() {
            return prop1;
        }
    }

}
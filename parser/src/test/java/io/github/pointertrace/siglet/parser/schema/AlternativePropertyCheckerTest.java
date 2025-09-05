package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.SchemaValidationError;
import io.github.pointertrace.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.array;
import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.text;
import static org.junit.jupiter.api.Assertions.*;

class AlternativePropertyCheckerTest {

    private ObjectChecker objectCheck;

    private YamlParser parser;

    private String expected;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();
    }


    @Test
    void describe() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        expected = "object\n" +
                   "  alternative property\n" +
                   "    alternative\n" +
                   "      property  (name:prop1, required:true)\n" +
                   "        text\n" +
                   "      property  (name:prop1, required:true)\n" +
                   "        array\n" +
                   "          text";

        assertEquals(expected, objectCheck.describe());
    }

    @Test
    void check_firstAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("prop1: textNode-value\n");

        objectCheck.check(node);

        Object value = node.getValue();
        assertNotNull(value);
        Bean bean = assertInstanceOf(Bean.class, value);
        assertEquals(Arrays.asList("textNode-value"), bean.getProp1());
    }

    @Test
    void check_secondAlternative() throws SchemaValidationError {

        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("prop1:\n" +
                                 "- first-value\n" +
                                 "- second-value\n");

        objectCheck.check(node);

        Object value = assertInstanceOf(Bean.class, node.getValue());
        assertNotNull(value);
        Bean bean = assertInstanceOf(Bean.class, value);
        assertEquals(Arrays.asList("first-value", "second-value"), bean.getProp1());
    }

    @Test
    void check_noValidOption() throws SchemaValidationError {
        objectCheck = new ObjectChecker(Bean::new, true,
                new AlternativePropertyChecker("prop1", true,
                        new PropertyChecker(Bean::setProp1AsValue, "prop1", true, text()),
                        new PropertyChecker(Bean::setProp1, "prop1", true, array(text()))));

        Node node = parser.parse("prop1:\n" +
                                 "- 10\n");

        MultipleSchemaValidationError ex = assertThrowsExactly(MultipleSchemaValidationError.class,
                () -> objectCheck.check(node));

        expected = "(1:1) None of alternatives are valid because:\n" +
                   "  (1:1) property prop1 is not valid because:\n" +
                   "    (1:1) is not a text value!\n" +
                   "  (1:1) property prop1 is not valid because:\n" +
                   "    (2:3) array item is not valid because:\n" +
                   "      (2:3) is not a text value!";

        assertEquals(expected, ex.getMessage());

    }

    public static class Bean {

        private List<String> prop1;

        public void setProp1(List<String> prop1) {
            this.prop1 = prop1;
        }

        public void setProp1AsValue(String prop1) {
            this.prop1 = Arrays.asList(prop1);
        }

        public List<String> getProp1() {
            return prop1;
        }
    }

}
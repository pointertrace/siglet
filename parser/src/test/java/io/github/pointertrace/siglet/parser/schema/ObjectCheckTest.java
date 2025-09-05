package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectCheckTest {

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
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        expected = "object\n" +
                   "  property  (name:prop1, required:true)\n" +
                   "    int\n" +
                   "  property  (name:prop2, required:true)\n" +
                   "    text";

        assertEquals(expected, objectCheck.describe());
    }
    @Test
    void check() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("prop1: 1\n" +
                                 "prop2: textNode value\n");

        objectCheck.check(node);
    }


    @Test
    void check_notObject() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("- a\n" +
                                 "- b\n");

        SingleSchemaValidationError ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("(1:1) expecting an object!", ex.getMessage());
        assertEquals(Location.of(1,1), ex.getLocation());
    }

    @Test
    void check_onePropertyNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("prop1: 1\n" +
                                 "prop2: test\n" +
                                 "prop5: 3\n");

        SingleSchemaValidationError ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("(3:1) property prop5 is not expected!", ex.getMessage());
        assertEquals(Location.of(3,1), ex.getLocation());
    }

    @Test
    void check_twoPropertiesNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("prop1: 1\n" +
                                 "prop2: test\n" +
                                 "prop5: 3\n" +
                                 "prop6: 3\n");

        MultipleSchemaValidationError ex = assertThrows(MultipleSchemaValidationError.class,
        () -> objectCheck.check(node));

        expected = "(1:1) Object is not valid because:\n" +
                   "  (3:1) property prop5 is not expected!\n" +
                   "  (4:1) property prop6 is not expected!";

        assertEquals(expected, ex.getMessage());


    }



    public static class Bean {
    }
}
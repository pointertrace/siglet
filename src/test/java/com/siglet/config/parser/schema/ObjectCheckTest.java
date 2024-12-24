package com.siglet.config.parser.schema;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectCheckTest {

    private ObjectChecker objectCheck;

    private ConfigParser parser;

    private String expected;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    void descibe() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        expected = """
                object
                  property  (name:prop1, required:true)
                    int
                  property  (name:prop2, required:true)
                    text""";

        assertEquals(expected, objectCheck.describe());
    }
    @Test
    void check() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("""
                prop1: 1
                prop2: text value
                """);

        objectCheck.check(node);
    }


    @Test
    void check_notObject() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("""
                - a
                - b
                """);

        var ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("(1:1) expecting an object!", ex.explain());
        assertEquals(Location.of(1,1), ex.getLocation());
    }

    @Test
    void check_onePropertyNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("""
                prop1: 1
                prop2: test
                prop5: 3
                """);

        var ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("property prop5 is not expected!", ex.getMessage());
        assertEquals(Location.of(3,1), ex.getLocation());
    }

    @Test
    void check_twoPropertiesNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        Node node = parser.parse("""
                prop1: 1
                prop2: test
                prop5: 3
                prop6: 3
                """);

        MultipleSchemaValidationError ex = assertThrows(MultipleSchemaValidationError.class,
        () -> objectCheck.check(node));

        expected = """
        (1:1) Object is not valid because:
          (3:1) property prop5 is not expected!
          (4:1) property prop6 is not expected!""";

        assertEquals(expected, ex.explain());


    }



    public static class Bean extends Item {
    }
}
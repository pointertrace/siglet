package com.siglet.config.parser.schema;

import com.siglet.config.item.Item;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectCheckTest {

    private ObjectChecker objectCheck;

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    public void check() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        ConfigNode node = parser.parse("""
                prop1: 1
                prop2: text value
                """);

        objectCheck.check(node);
    }


    @Test
    public void check_notObject() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        ConfigNode node = parser.parse("""
                - a
                - b
                """);

        var ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("expecting an object", ex.getMessage());
        assertEquals(Location.of(1,1), ex.getLocation());
    }

    @Test
    public void check_onePropertyNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        ConfigNode node = parser.parse("""
                prop1: 1
                prop2: test
                prop5: 3
                """);

        var ex = assertThrows(SingleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("property prop5 is not expected", ex.getMessage());
        assertEquals(Location.of(3,1), ex.getLocation());
    }

    @Test
    public void check_twoPropertiesNotDefined() {

        objectCheck = new ObjectChecker(Bean::new, true,
                new PropertyChecker("prop1", true, new IntChecker()),
                new PropertyChecker("prop2", true, new TextChecker()));

        ConfigNode node = parser.parse("""
                prop1: 1
                prop2: test
                prop5: 3
                prop6: 3
                """);

        var ex = assertThrows(MultipleSchemaValidationError.class, () -> objectCheck.check(node));
        assertEquals("properties prop5 and prop6 are not expected", ex.getMessage());

        assertEquals(2, ex.getValidationExceptions().size());

        assertEquals("property prop5 is not expected", ex.getValidationExceptions().getFirst().getMessage());
        assertEquals(Location.of(3,1), ex.getValidationExceptions().getFirst().getLocation());

        assertEquals("property prop6 is not expected", ex.getValidationExceptions().get(1).getMessage());
        assertEquals(Location.of(4,1), ex.getValidationExceptions().get(1).getLocation());
    }



    public static class Bean extends Item {
        private int prop1;
        private String prop2;

        public int getProp1() {
            return prop1;
        }

        public void setProp1(int prop1) {
            this.prop1 = prop1;
        }

        public String getProp2() {
            return prop2;
        }

        public void setProp2(String prop2) {
            this.prop2 = prop2;
        }
    }
}
package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ObjectCheckTest {

    private ObjectChecker objectCheck;

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();
    }

    @Test
    public void check() throws SchemaValidationException {

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
    public void check_requiredPropertyNotFound() {

//        objectCheck = new ObjectCheck(true,
//                new PropertyCheck("prop1", true, new IntCheck()),
//                new PropertyCheck("prop2", true, new TextChecker()),
//                new PropertyCheck("prop3", true, new TextChecker()));
//
//        ConfigNode node = parser.parse("""
//                prop1: 1
//                prop2: text value
//                """);
//
//        SingleSchemaValidationException ex = assertThrowsExactly(SingleSchemaValidationException.class,
//                () -> objectCheck.check(node));
//
//        assertEquals("must have a prop3 property!", ex.getMessage());
//        assertEquals(Location.create(1, 1), ex.getLocation());
    }

    @Test
    public void check_strictWithOneNotDefinedProperty() {

//        objectCheck = new ObjectCheck(true,
//                new PropertyCheck("prop1", true, new IntCheck()),
//                new PropertyCheck("prop2", true, new TextChecker()));
//
//        ConfigNode node = parser.parse("""
//                prop1: 1
//                prop2: text value
//                prop3: other text value
//                """);
//
//        SingleSchemaValidationException ex = assertThrowsExactly(SingleSchemaValidationException.class,
//                () -> objectCheck.check(node));
//
//        assertEquals("property prop3 not defined!", ex.getMessage());
//        assertEquals(Location.create(3, 1), ex.getLocation());
    }

    @Test
    public void check_strictWithTwoNotDefinedProperty() {
//
//        objectCheck = new ObjectCheck(true,
//                new PropertyCheck("prop1", true, new IntCheck()),
//                new PropertyCheck("prop2", true, new TextChecker()));
//
//        ConfigNode node = parser.parse("""
//                prop1: 1
//                prop2: text value
//                prop3: other text value
//                prop4: 2
//                """);
//
//        MultipleSchemaValidationError ex = assertThrowsExactly(MultipleSchemaValidationError.class,
//                () -> objectCheck.check(node));
//
//        assertEquals("properties prop3, prop4 not defined!", ex.getMessage());
//        List<SingleSchemaValidationException> validationExceptions = ex.getValidationExceptions();
//        assertEquals(2, validationExceptions.size());
//
//        assertEquals("property prop3 not defined!", validationExceptions.get(0).getMessage());
//        assertEquals(Location.create(3, 1), validationExceptions.get(0).getLocation());
//
//        assertEquals("property prop4 not defined!", validationExceptions.get(1).getMessage());
//        assertEquals(Location.create(4, 1), validationExceptions.get(1).getLocation());
    }

    public static class Bean {
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
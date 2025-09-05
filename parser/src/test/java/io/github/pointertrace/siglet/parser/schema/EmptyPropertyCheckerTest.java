package io.github.pointertrace.siglet.parser.schema;

import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.node.ObjectNode;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EmptyPropertyCheckerTest {

    @Test
    void describe() {

        EmptyPropertyChecker emptyPropertyChecker = new EmptyPropertyChecker("property");

        assertEquals("empty", emptyPropertyChecker.describe());
    }


    @Test
    void check_invalid() {

        EmptyPropertyChecker emptyPropertyChecker = new EmptyPropertyChecker("prop1");

        YamlParser parser = new YamlParser();

        Node node = parser.parse("obj:\n" +
                                 "  prop1: value1\n");


        ObjectNode objectNode =(ObjectNode) ((ObjectNode) node).get("obj");

        assertThrowsExactly(SingleSchemaValidationError.class, () -> emptyPropertyChecker.check(objectNode));

    }

    @Test
    void check() {

        EmptyPropertyChecker emptyPropertyChecker = new EmptyPropertyChecker("prop1");

        YamlParser parser = new YamlParser();

        Node node = parser.parse("obj:\n" +
                                 "  other-prop: value1\n");


        ObjectNode objectNode =(ObjectNode) ((ObjectNode) node).get("obj");

        emptyPropertyChecker.check(objectNode);

    }
}
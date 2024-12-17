package com.siglet.config.parser.schema;

import com.siglet.config.item.ValueItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainCheckerTest {


    @Test
    void check() {
        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));


        var yaml = "a";

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        checker.check(root);

        var value = assertInstanceOf(ValueItem.class, root.getValue());

        assertEquals("a", value.getValue());

    }

    @Test
    void check_notInSet() {
        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));


        var yaml = "d";

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        var ex = assertThrowsExactly(SingleSchemaValidationError.class,() -> { checker.check(root); });

        assertEquals("must be one of [a, b, c]", ex.getMessage());
        assertEquals(Location.of(1,1), ex.getLocation());


    }
}
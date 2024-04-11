package com.siglet.config.parser.schema;

import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.locatednode.Location;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainCheckerTest {


    @Test
    public void check() throws Exception {
        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));


        var yaml = "a";

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        checker.check(root);

        assertEquals("a", root.getValue());

    }

    @Test
    public void check_notInSet() throws Exception {
        TextChecker checker = new TextChecker(new DomainChecker(() -> List.of("a","b","c")));


        var yaml = "d";

        ConfigParser parser = new ConfigParser();

        ConfigNode root = parser.parse(yaml);

        var ex = assertThrowsExactly(SingleSchemaValidationError.class,() -> { checker.check(root); });

        assertEquals("must be in [a, b, c]", ex.getMessage());
        assertEquals(Location.create(1,1), ex.getLocation());


    }
}
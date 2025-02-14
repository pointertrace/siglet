package com.siglet.pipeline.processor.common.router;

import com.siglet.config.item.SigletItem;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import com.siglet.config.parser.schema.NodeChecker;
import com.siglet.config.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class RouterConfigTest {

    private ConfigParser parser;

    @BeforeEach
    public void setUp() {
        parser = new ConfigParser();

    }


    @Test
    void describe() {

        String configTxt = """
                config:
                  routes:
                    - when: first expression
                      to: first destination
                    - when: second expression
                      to: second destination
                  default: third destination
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new RouterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet.getConfig());
        RouterConfig routerConfig = assertInstanceOf(RouterConfig.class, siglet.getConfig());

        String expected = """
                (1:1)  routerConfig:
                  (2:3)  routes:
                    (3:7)  route:
                      (3:13)  when: first expression
                      (4:11)  to: first destination
                    (5:7)  route:
                      (5:13)  when: second expression
                      (6:11)  to: second destination
                  (7:12)  defaultRoute: third destination""";

        assertEquals(expected, routerConfig.describe(0));

    }


    @Test
    void getValue() {


        String configTxt = """
                config:
                  routes:
                    - when: first expression
                      to: first destination
                    - when: second expression
                      to: second destination
                  default: third destination
                """;

        Node node = parser.parse(configTxt);

        NodeChecker checker = strictObject(SigletItem::new,
                (PropertyChecker) new RouterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        SigletItem siglet = assertInstanceOf(SigletItem.class, value);

        assertNotNull(siglet.getConfig());
        RouterConfig routerConfig = assertInstanceOf(RouterConfig.class, siglet.getConfig());

        assertEquals(Location.of(1, 1), routerConfig.getLocation());

        assertEquals("third destination", routerConfig.getDefaultRoute());
        assertEquals(Location.of(7, 12), routerConfig.getDefaultRouteLocation());

        List<Route> routes = routerConfig.getRoutes();
        assertEquals(2, routes.size());

        assertEquals(Location.of(3,7), routes.getFirst().getLocation());
        assertEquals("first expression", routes.getFirst().getWhen());
        assertEquals(Location.of(3,13), routes.getFirst().getWhenLocation());

        assertEquals("first destination", routes.getFirst().getTo());
        assertEquals(Location.of(4,11), routes.getFirst().getToLocation());

        assertEquals(Location.of(5,7), routes.get(1).getLocation());
        assertEquals("second expression", routes.get(1).getWhen());
        assertEquals(Location.of(5,13), routes.get(1).getWhenLocation());

        assertEquals("second destination", routes.get(1).getTo());
        assertEquals(Location.of(6,11), routes.get(1).getToLocation());
    }
}
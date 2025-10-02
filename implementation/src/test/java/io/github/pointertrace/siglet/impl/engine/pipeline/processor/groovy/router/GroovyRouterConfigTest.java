package io.github.pointertrace.siglet.impl.engine.pipeline.processor.groovy.router;

import io.github.pointertrace.siglet.impl.config.raw.ProcessorConfig;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.NodeChecker;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import io.github.pointertrace.siglet.parser.schema.PropertyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siglet.parser.schema.SchemaFactory.strictObject;
import static org.junit.jupiter.api.Assertions.*;

class GroovyRouterConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();

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

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyRouterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(siglet.getConfig());
        GroovyRouterConfig groovyRouterConfig = assertInstanceOf(GroovyRouterConfig.class, siglet.getConfig());

        String expected = """
                (1:1)  groovyRouterConfig:
                  (2:3)  routes:
                    (3:7)  route:
                      (3:13)  when: first expression
                      (4:11)  to: first destination
                    (5:7)  route:
                      (5:13)  when: second expression
                      (6:11)  to: second destination
                  (7:12)  defaultRoute: third destination""";

        assertEquals(expected, groovyRouterConfig.describe(0));

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

        NodeChecker checker = strictObject(ProcessorConfig::new,
                (PropertyChecker) new GroovyRouterDefinition().getChecker());

        checker.check(node);
        Object value = node.getValue();

        assertNotNull(node);
        ProcessorConfig siglet = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(siglet.getConfig());
        GroovyRouterConfig groovyRouterConfig = assertInstanceOf(GroovyRouterConfig.class, siglet.getConfig());

        assertEquals(Location.of(1, 1), groovyRouterConfig.getLocation());

        assertEquals("third destination", groovyRouterConfig.getDefaultRoute());
        assertEquals(Location.of(7, 12), groovyRouterConfig.getDefaultRouteLocation());

        List<RouteConfig> routeConfigs = groovyRouterConfig.getRoutes();
        assertEquals(2, routeConfigs.size());

        assertEquals(Location.of(3, 7), routeConfigs.getFirst().getLocation());
        assertEquals("first expression", routeConfigs.getFirst().getWhen());
        assertEquals(Location.of(3, 13), routeConfigs.getFirst().getWhenLocation());

        assertEquals("first destination", routeConfigs.getFirst().getTo());
        assertEquals(Location.of(4, 11), routeConfigs.getFirst().getToLocation());

        assertEquals(Location.of(5, 7), routeConfigs.get(1).getLocation());
        assertEquals("second expression", routeConfigs.get(1).getWhen());
        assertEquals(Location.of(5, 13), routeConfigs.get(1).getWhenLocation());

        assertEquals("second destination", routeConfigs.get(1).getTo());
        assertEquals(Location.of(6, 11), routeConfigs.get(1).getToLocation());
    }

}
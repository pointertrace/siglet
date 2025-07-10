package com.siglet.container.config.raw;

import com.siglet.parser.Node;
import com.siglet.parser.YamlParser;
import com.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.container.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class GlobalConfigTest {

    private YamlParser parser;

    @BeforeEach
    public void setUp() {
        parser = new YamlParser();
    }

    @Test
    void describe() {

        var config = """
                queue-size: 1
                thread-pool-size: 2
                span-object-pool-size: 3
                metric-object-pool-size: 4
                """;


        Node node = parser.parse(config);

        globalConfigChecker().check(node);

        Object value = node.getValue();
        GlobalConfig globalConfig = assertInstanceOf(GlobalConfig.class, value);

        assertNotNull(globalConfig);

        String expected = """
               (1:1)  GlobalConfig
                 (1:13)  queue-size: 1
                 (2:19)  thread-pool-size: 2
                 (3:24)  span-object-pool-size: 3
                 (4:26)  metric-object-pool-size: 4""";

        assertEquals(expected, globalConfig.describe(0));
    }

    @Test
    void getValue() {

        var config = """
                queue-size: 1
                thread-pool-size: 2
                span-object-pool-size: 3
                metric-object-pool-size: 4""";

        Node node = parser.parse(config);

        globalConfigChecker().check(node);

        Object value = node.getValue();

        GlobalConfig globalConfig = assertInstanceOf(GlobalConfig.class, value);

        assertEquals(Location.of(1, 1), globalConfig.getLocation());

        assertEquals(1, globalConfig.getQueueSize());
        assertEquals(Location.of(1, 13), globalConfig.getQueueSizeLocation());

        assertEquals(2, globalConfig.getThreadPoolSize());
        assertEquals(Location.of(2, 19), globalConfig.getThreadPoolSizeLocation());

        assertEquals(3, globalConfig.getSpanObjectPoolSize());
        assertEquals(Location.of(3, 24), globalConfig.getSpanObjectPoolSizeLocation());

        assertEquals(4, globalConfig.getMetricObjectPoolSize());
        assertEquals(Location.of(4, 26), globalConfig.getMetricObjectPoolSizeLocation());

    }

}
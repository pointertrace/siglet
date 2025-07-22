package com.siglet.container.config.raw;

import com.siglet.SigletError;
import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import com.siglet.container.engine.pipeline.processor.groovy.action.GroovyActionConfig;
import com.siglet.parser.Node;
import com.siglet.parser.YamlParser;
import com.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.processorChecker;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorConfigTest {

    private YamlParser parser;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();

    }


    @Test
    void describe_toList() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);

        String expected = """
                (1:1)  processorConfig:
                  (1:7)  name: spanlet node
                  (2:7)  kind: SPANLET
                  (3:1)  to:
                    (4:3)  first exporter
                    (5:3)  second exporter
                  (6:7)  type: groovy-action
                  (7:1) config:
                    (7:1)  groovyActionConfig:
                      (8:11)  action: otelSignalType.name = otelSignalType.name +"-suffix" """;

        assertEquals(expected, processorConfig.describe());

    }

    @Test
    void describe_eventLoopConfig() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                queue-size: 10
                thread-pool-size: 20
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);

        String expected = """
                (1:1)  processorConfig:
                  (1:7)  name: spanlet node
                  (2:7)  kind: SPANLET
                  (3:1)  to:
                    (4:3)  first exporter
                    (5:3)  second exporter
                  (6:7)  type: groovy-action
                  (7:13)  queueSize: 10
                  (8:19)  threadPoolSize: 20
                  (9:1) config:
                    (9:1)  groovyActionConfig:
                      (10:11)  action: otelSignalType.name = otelSignalType.name +"-suffix" """;

        assertEquals(expected, processorConfig.describe());

    }

    @Test
    void getValue_toList() {


        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);
        assertEquals("spanlet node", processorConfig.getName());
        assertEquals(Location.of(1, 7), processorConfig.getNameLocation());

        assertEquals(ProcessorKind.SPANLET, processorConfig.getProcessorKind());
        assertEquals(Location.of(2, 7), processorConfig.getKindLocation());

        List<LocatedString> to = processorConfig.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3, 1), processorConfig.getToLocation());

        assertEquals(2, to.size());
        assertEquals("first exporter", to.getFirst().getValue());
        assertEquals(Location.of(4, 3), to.getFirst().getLocation());

        assertEquals("second exporter", to.get(1).getValue());
        assertEquals(Location.of(5, 3), to.get(1).getLocation());

        assertEquals("groovy-action", processorConfig.getType());
        assertEquals(Location.of(6, 7), processorConfig.getTypeLocation());

        assertNull(processorConfig.getQueueSize());
        assertNull(processorConfig.getQueueSizeLocation());

        assertNull(processorConfig.getThreadPoolSize());
        assertNull(processorConfig.getThreadPoolSizeLocation());

        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, processorConfig.getConfig());
        assertNotNull(groovyActionConfig);
        assertEquals(Location.of(7, 1), processorConfig.getConfigLocation());
        assertEquals("otelSignalType.name = otelSignalType.name +\"-suffix\"", groovyActionConfig.getAction());

    }


    @Test
    void describe_toSingle() {

        String configTxt = """
                name: spanlet-node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        String expected = """
                (1:1)  processorConfig:
                  (1:7)  name: spanlet-node
                  (2:7)  kind: SPANLET
                  (3:5)  to:
                    (3:5)  exporter
                  (4:7)  type: groovy-action
                  (5:1) config:
                    (5:1)  groovyActionConfig:
                      (6:11)  action: otelSignalType.name = otelSignalType.name +"-suffix" """;

        assertEquals(expected, processorConfig.describe());

    }


    @Test
    void getValue_toSingle() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);

        assertNotNull(processorConfig);
        assertEquals("spanlet node", processorConfig.getName());
        assertEquals(Location.of(1, 7), processorConfig.getNameLocation());

        assertEquals(ProcessorKind.SPANLET, processorConfig.getProcessorKind());
        assertEquals(Location.of(2, 7), processorConfig.getKindLocation());

        List<LocatedString> to = processorConfig.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3, 5), processorConfig.getToLocation());

        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());
        assertEquals(Location.of(3, 5), to.getFirst().getLocation());

        assertEquals("groovy-action", processorConfig.getType());
        assertEquals(Location.of(4, 7), processorConfig.getTypeLocation());

        assertNull(processorConfig.getQueueSize());
        assertNull(processorConfig.getQueueSizeLocation());

        assertNull(processorConfig.getThreadPoolSize());
        assertNull(processorConfig.getThreadPoolSizeLocation());

        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, processorConfig.getConfig());
        assertNotNull(groovyActionConfig);
        assertEquals(Location.of(5, 1), processorConfig.getConfigLocation());
        assertEquals("otelSignalType.name = otelSignalType.name +\"-suffix\"", groovyActionConfig.getAction());


    }

    @Test
    void getValue_eventLoopConfig() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                queue-size: 10
                thread-pool-size: 20
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        assertNotNull(processorConfig);

        assertNotNull(processorConfig);
        assertEquals("spanlet node", processorConfig.getName());
        assertEquals(Location.of(1, 7), processorConfig.getNameLocation());

        assertEquals(ProcessorKind.SPANLET, processorConfig.getProcessorKind());
        assertEquals(Location.of(2, 7), processorConfig.getKindLocation());

        List<LocatedString> to = processorConfig.getTo();
        assertNotNull(to);
        assertEquals(Location.of(3, 5), processorConfig.getToLocation());

        assertEquals(1, to.size());
        assertEquals("exporter", to.getFirst().getValue());
        assertEquals(Location.of(3, 5), to.getFirst().getLocation());

        assertEquals("groovy-action", processorConfig.getType());
        assertEquals(Location.of(4, 7), processorConfig.getTypeLocation());

        assertEquals(10, processorConfig.getQueueSize());
        assertEquals(Location.of(5, 13), processorConfig.getQueueSizeLocation());

        assertEquals(20, processorConfig.getThreadPoolSize());
        assertEquals(Location.of(6, 19), processorConfig.getThreadPoolSizeLocation());

        GroovyActionConfig groovyActionConfig = assertInstanceOf(GroovyActionConfig.class, processorConfig.getConfig());
        assertNotNull(groovyActionConfig);
        assertEquals(Location.of(7, 1), processorConfig.getConfigLocation());
        assertEquals("otelSignalType.name = otelSignalType.name +\"-suffix\"", groovyActionConfig.getAction());

    }

    @Test
    void getQueueSize_rawConfigNull() {
        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        SigletError e = assertThrows(SigletError.class, processorConfig::getQueueSizeConfig);
        assertEquals("rawConfig is null", e.getMessage());

    }

    @Test
    void getQueueSize_rawConfigGlobalConfigNull() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        processorConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), processorConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigEmpty() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(QueueSizeConfig.defaultConfig().getQueueSize(), processorConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigDefined() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(10, processorConfig.getQueueSizeConfig().getQueueSize());

    }

    @Test
    void getQueueSize_rawConfigNotNullGlobalConfigDefinedProcessorDefined() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                queue-size: 20
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setQueueSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(20, processorConfig.getQueueSizeConfig().getQueueSize());

    }


    @Test
    void getThreadPoolSize_rawConfigGlobalConfigNull() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        processorConfig.setRawConfig(rawConfig);

        assertEquals(ThreadPoolSizeConfig.defaultConfig().getThreadPoolSize(),
                processorConfig.getThreadPoolSizeConfig().getThreadPoolSize());

    }

    @Test
    void getThreadPoolSize_rawConfigNotNullGlobalConfigEmpty() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(ThreadPoolSizeConfig.defaultConfig().getThreadPoolSize(),
                processorConfig.getThreadPoolSizeConfig().getThreadPoolSize());

    }

    @Test
    void getThreadPoolSize_rawConfigNotNullGlobalConfigDefined() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setThreadPoolSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(10, processorConfig.getThreadPoolSizeConfig().getThreadPoolSize());

    }

    @Test
    void getThreadPoolSize_rawConfigNotNullGlobalConfigDefinedProcessorDefined() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to: exporter
                type: groovy-action
                thread-pool-size: 20
                config:
                  action: otelSignalType.name = otelSignalType.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        RawConfig rawConfig = new RawConfig();
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setThreadPoolSize(10);
        rawConfig.setGlobalConfig(globalConfig);
        processorConfig.setRawConfig(rawConfig);

        assertEquals(20, processorConfig.getThreadPoolSizeConfig().getThreadPoolSize());

    }

}
package com.siglet.container.config.graph;

import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import com.siglet.parser.Node;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.container.config.ConfigCheckFactory.processorChecker;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorNodeTest {

    private YamlParser parser;

    private ProcessorNode processorNode;

    @BeforeEach
    void setUp() {
        parser = new YamlParser();

    }

    @Test
    void calculateEventLoopConfig_noProcessorConfigConfiguration() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        processorNode = new ProcessorNode(processorConfig);

        EventLoopConfig defaultConfig = EventLoopConfig.of(10,20);

        processorNode.calculateEventLoopConfig(defaultConfig);

        assertEquals(10, processorNode.getQueueSize());
        assertEquals(20, processorNode.getThreadPoolSize());

    }


    @Test
    void calculateEventLoopConfig_processorConfigConfiguration() {

        String configTxt = """
                name: spanlet node
                kind: spanlet
                to:
                - first exporter
                - second exporter
                type: groovy-action
                queue-size: 100
                thread-pool-size: 200
                config:
                  action: signal.name = signal.name +"-suffix"
                """;

        Node node = parser.parse(configTxt);

        processorChecker(new ProcessorTypeRegistry()).check(node);

        Object value = node.getValue();
        ProcessorConfig processorConfig = assertInstanceOf(ProcessorConfig.class, value);

        processorNode = new ProcessorNode(processorConfig);

        EventLoopConfig defaultConfig = EventLoopConfig.of(10,20);

        processorNode.calculateEventLoopConfig(defaultConfig);

        assertEquals(100, processorNode.getQueueSize());
        assertEquals(200, processorNode.getThreadPoolSize());

    }

}
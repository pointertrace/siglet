package com.siglet.container.engine;

import com.siglet.container.config.graph.ProcessorNode;
import com.siglet.container.config.raw.EventLoopConfig;
import com.siglet.container.config.raw.ProcessorConfig;
import com.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import com.siglet.parser.Node;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.processorChecker;
import static com.siglet.container.config.ConfigCheckFactory.rawConfigChecker;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContextTest {

    Context context;

    @Test
    void getEventLoopConfig_default() {

        var yaml = """
                receivers:
                - grpc: first receiver
                  address: localhost:8080
                - grpc: second receiver
                  address: localhost:8081
                exporters:
                - grpc: first exporter
                  address: localhost:8080
                - grpc: second exporter
                  address: localhost:8081
                pipelines:
                - name: pipeline name
                  signal: trace
                  from: first receiver
                  start: spanlet name
                  processors:
                  - name: spanlet name
                    kind: spanlet
                    to: first exporter
                    type: groovy-action
                    config:
                      action: action-value
                """;

        context = new Context(yaml, List.of());

        ProcessorNode processorNode = context.getGraph().getNodeByNameAndType("spanlet name", ProcessorNode.class);

        assertEquals(1_000, processorNode.getQueueSize());
        assertEquals(Runtime.getRuntime().availableProcessors(), processorNode.getThreadPoolSize());

    }

    @Test
    void getEventLoopConfig_globalConfig() {

        var yaml = """
                global:
                    queue-size: 10
                    thread-pool-size: 20
                receivers:
                - grpc: first receiver
                  address: localhost:8080
                - grpc: second receiver
                  address: localhost:8081
                exporters:
                - grpc: first exporter
                  address: localhost:8080
                - grpc: second exporter
                  address: localhost:8081
                pipelines:
                - name: pipeline name
                  signal: trace
                  from: first receiver
                  start: spanlet name
                  processors:
                  - name: spanlet name
                    kind: spanlet
                    to: first exporter
                    type: groovy-action
                    config:
                      action: action-value
                """;

        context = new Context(yaml, List.of());

        ProcessorNode processorNode = context.getGraph().getNodeByNameAndType("spanlet name", ProcessorNode.class);

        assertEquals(10, processorNode.getQueueSize());
        assertEquals(20, processorNode.getThreadPoolSize());

    }

    @Test
    void getEventLoopConfig_specific() {

        var yaml = """
                global:
                  queue-size: 10
                  thread-pool-size: 20
                receivers:
                - grpc: first receiver
                  address: localhost:8080
                - grpc: second receiver
                  address: localhost:8081
                exporters:
                - grpc: first exporter
                  address: localhost:8080
                - grpc: second exporter
                  address: localhost:8081
                pipelines:
                - name: pipeline name
                  signal: trace
                  from: first receiver
                  start: spanlet name
                  processors:
                  - name: spanlet name
                    kind: spanlet
                    to: first exporter
                    type: groovy-action
                    queue-size: 100
                    thread-pool-size: 200
                    config:
                      action: action-value
                """;

        Node node = new YamlParser().parse(yaml);
        rawConfigChecker(new ProcessorTypeRegistry()).check(node);

        context = new Context(yaml, List.of());

        ProcessorNode processorNode = context.getGraph().getNodeByNameAndType("spanlet name", ProcessorNode.class);

        assertEquals(100, processorNode.getQueueSize());
        assertEquals(200, processorNode.getThreadPoolSize());

    }
}
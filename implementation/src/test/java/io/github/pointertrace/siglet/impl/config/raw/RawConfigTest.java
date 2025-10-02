package io.github.pointertrace.siglet.impl.config.raw;

import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.YamlParser;
import io.github.pointertrace.siglet.parser.located.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.pointertrace.siglet.impl.config.ConfigCheckFactory.rawConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class RawConfigTest {

    private YamlParser configParser;

    @BeforeEach
    void setUp() {
        configParser = new YamlParser();
    }

    @Test
    void getValue() {


        var yaml = """
                global:
                  queue-size: 1
                  thread-pool-size: 2
                  span-object-pool-size: 3
                  metric-object-pool-size: 4
                receivers:
                - grpc: first receiver
                  config:
                    address: localhost:8080
                - grpc: second receiver
                  config:
                    address: localhost:8081
                exporters:
                - grpc: first exporter
                  config:
                    address: localhost:8080
                - grpc: second exporter
                  config:
                    address: localhost:8081
                pipelines:
                - name: pipeline name
                  from: first receiver
                  start: spanlet name
                  processors:
                  - spanlet-groovy-action: spanlet name
                    to: first exporter
                    config:
                      action: action-value
                """;


        Node node = configParser.parse(yaml);

        rawConfigChecker(new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry()).check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        RawConfig rawConfig = assertInstanceOf(RawConfig.class, conf);

        assertNotNull(rawConfig.getGlobalConfig());
        GlobalConfig globalConfig = rawConfig.getGlobalConfig();
        assertEquals(Location.of(1, 1), globalConfig.getLocation());
        assertEquals(1, globalConfig.getQueueSize());
        assertEquals(Location.of(2, 15), globalConfig.getQueueSizeLocation());
        assertEquals(2, globalConfig.getThreadPoolSize());
        assertEquals(Location.of(3, 21), globalConfig.getThreadPoolSizeLocation());
        assertEquals(3, globalConfig.getSpanObjectPoolSize());
        assertEquals(Location.of(4, 26), globalConfig.getSpanObjectPoolSizeLocation());
        assertEquals(4, globalConfig.getMetricObjectPoolSize());
        assertEquals(Location.of(5, 28), globalConfig.getMetricObjectPoolSizeLocation());

        List<ReceiverConfig> receivers = rawConfig.getReceivers();
        assertNotNull(receivers);
        assertEquals(2, receivers.size());
        assertEquals(Location.of(6,1), rawConfig.getReceiversLocation());

        assertEquals("first receiver", receivers.getFirst().getName());
        assertEquals("second receiver", receivers.get(1).getName());

        List<ExporterConfig> exporters = rawConfig.getExporters();
        assertNotNull(exporters);
        assertEquals(2, exporters.size());
        assertEquals(Location.of(13,1), rawConfig.getExportersLocation());

        assertEquals("first exporter", exporters.getFirst().getName());
        assertEquals("second exporter", exporters.get(1).getName());

        List<PipelineConfig> pipelines = rawConfig.getPipelines();
        assertNotNull(pipelines);
        assertEquals(1, pipelines.size());
        assertEquals(Location.of(20,1), rawConfig.getPipelinesLocation());

        assertEquals("pipeline name",pipelines.getFirst().getName());

    }

    @Test
    void describe() throws Exception {


        var yaml = """
                global:
                  queue-size: 1
                  thread-pool-size: 2
                  span-object-pool-size: 3
                  metric-object-pool-size: 4
                receivers:
                - grpc: first receiver
                  config:
                    address: localhost:8080
                - grpc: second receiver
                  config:
                    address: localhost:8081
                exporters:
                - grpc: first exporter
                  config:
                    address: localhost:8080
                - grpc: second exporter
                  config:
                    address: localhost:8081
                pipelines:
                - name: pipeline name
                  from: first receiver
                  start: spanlet name
                  processors:
                  - spanlet-groovy-action: spanlet name
                    to: first exporter
                    config:
                      action: action-value
                """;

        Node node = configParser.parse(yaml);

        rawConfigChecker(new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry()).check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        RawConfig rawConfig = assertInstanceOf(RawConfig.class, conf);

        String expected = """
            (1:1)  RawConfig:
              (1:1)  GlobalConfig
                (2:15)  queue-size: 1
                (3:21)  thread-pool-size: 2
                (4:26)  span-object-pool-size: 3
                (5:28)  metric-object-pool-size: 4
              (6:1)  receivers:
                (7:3)  receiverConfig:
                  (7:9)  name: first receiver
                  (7:3)  type: grpc
                  (8:3)  config: (OtelGrpcReceiverConfig)
                    (9,14)  address: localhost/127.0.0.1:8080
                (10:3)  receiverConfig:
                  (10:9)  name: second receiver
                  (10:3)  type: grpc
                  (11:3)  config: (OtelGrpcReceiverConfig)
                    (12,14)  address: localhost/127.0.0.1:8081
              (13:1)  exporters:
                (14:3)  exporterConfig:
                  (14:9)  name: first exporter
                  (14:3)  type: grpc
                  (15:3)  config: (OtelGrpcExporterConfig)
                    (16:14)  address: localhost/127.0.0.1:8080
                (17:3)  exporterConfig:
                  (17:9)  name: second exporter
                  (17:3)  type: grpc
                  (18:3)  config: (OtelGrpcExporterConfig)
                    (19:14)  address: localhost/127.0.0.1:8081
              (20:1)  pipelines:
                (21:3)  PipelineConfig:
                  (21:9)  name: pipeline name
                  (22:9)  from: first receiver
                  (23:10)  start:
                    (23:10)  spanlet name
                  (24:3)  processors:
                    (25:5)  processorConfig:
                      (25:28)  name: spanlet name
                      (25:5)  type: spanlet-groovy-action
                      (26:9)  to:
                        (26:9)  first exporter
                      (27:5) config:
                        (27:5)  groovyActionConfig:
                          (28:15)  action: action-value""";

        assertEquals(expected, rawConfig.describe());


    }
}
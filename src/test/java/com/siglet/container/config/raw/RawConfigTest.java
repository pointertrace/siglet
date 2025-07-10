package com.siglet.container.config.raw;

import com.siglet.container.engine.pipeline.processor.ProcessorTypes;
import com.siglet.parser.Node;
import com.siglet.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.rawConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class RawConfigTest {

    private YamlParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new YamlParser();
    }

    @Test
    void getValue() throws Exception {


        var yaml = """
                global:
                  queue-size: 1
                  thread-pool-size: 2
                  span-object-pool-size: 3
                  metric-object-pool-size: 4
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


        Node node = configParser.parse(yaml);

        rawConfigChecker(new ProcessorTypes()).check(node);

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
        assertEquals(Location.of(11,1), rawConfig.getExportersLocation());

        assertEquals("first exporter", exporters.getFirst().getName());
        assertEquals("second exporter", exporters.get(1).getName());

        List<PipelineConfig> pipelines = rawConfig.getPipelines();
        assertNotNull(pipelines);
        assertEquals(1, pipelines.size());
        assertEquals(Location.of(16,1), rawConfig.getPipelinesLocation());

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

        Node node = configParser.parse(yaml);

        rawConfigChecker(new ProcessorTypes()).check(node);

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
                    (7:3)  GrpcReceiverConfig
                      (7:9)  name: first receiver
                      (8:12)  address: localhost/127.0.0.1:8080
                    (9:3)  GrpcReceiverConfig
                      (9:9)  name: second receiver
                      (10:12)  address: localhost/127.0.0.1:8081
                  (11:1)  exporters:
                    (12:3)  GrpcExporterConfig
                      (12:9)  name: first exporter
                      (13:12)  address: localhost/127.0.0.1:8080
                    (14:3)  GrpcExporterConfig
                      (14:9)  name: second exporter
                      (15:12)  address: localhost/127.0.0.1:8081
                  (16:1)  pipelines:
                    (17:3)  PipelineConfig:
                      (17:9)  name: pipeline name
                      (18:11)  signal: TRACE
                      (19:9)  from: first receiver
                      (20:10)  start:
                        (20:10)  spanlet name
                      (21:3)  processors:
                        (22:5)  processorConfig:
                          (22:11)  name: spanlet name
                          (23:11)  kind: SPANLET
                          (24:9)  to:
                            (24:9)  first exporter
                          (25:11)  type: groovy-action
                          (26:5) config:
                            (26:5)  groovyActionConfig:
                              (27:15)  action: action-value""";

        assertEquals(expected, rawConfig.describe());


    }
}
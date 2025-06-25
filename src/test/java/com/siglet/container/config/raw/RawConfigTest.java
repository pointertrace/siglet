package com.siglet.container.config.raw;

import com.siglet.api.parser.Node;
import com.siglet.api.parser.located.Location;
import com.siglet.parser.YamlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.container.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class RawConfigTest {

    private YamlParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new YamlParser();
    }

    @Test
    void validateUniqueNames() throws Exception {


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


        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        RawConfig rawConfig = assertInstanceOf(RawConfig.class, conf);

        List<ReceiverConfig> receivers = rawConfig.getReceivers();
        assertNotNull(receivers);
        assertEquals(2, receivers.size());
        assertEquals(Location.of(1,1), rawConfig.getReceiversLocation());

        assertEquals("first receiver", receivers.getFirst().getName());
        assertEquals("second receiver", receivers.get(1).getName());

        List<ExporterConfig> exporters = rawConfig.getExporters();
        assertNotNull(exporters);
        assertEquals(2, exporters.size());
        assertEquals(Location.of(6,1), rawConfig.getExportersLocation());

        assertEquals("first exporter", exporters.getFirst().getName());
        assertEquals("second exporter", exporters.get(1).getName());

        List<PipelineConfig> pipelines = rawConfig.getPipelines();
        assertNotNull(pipelines);
        assertEquals(1, pipelines.size());
        assertEquals(Location.of(11,1), rawConfig.getPipelinesLocation());

        assertEquals("pipeline name",pipelines.getFirst().getName());

    }

    @Test
    void explain() throws Exception {


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

        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        RawConfig rawConfig = assertInstanceOf(RawConfig.class, conf);

        String expected = """
        (1:1)  config:
          (1:1)  receivers:
            (2:3)  GrpcReceiverConfig
              (2:9)  name: first receiver
              (3:12)  address: localhost/127.0.0.1:8080
            (4:3)  GrpcReceiverConfig
              (4:9)  name: second receiver
              (5:12)  address: localhost/127.0.0.1:8081
          (6:1)  exporters:
            (7:3)  GrpcExporterConfig
              (7:9)  name: first exporter
              (8:12)  address: localhost/127.0.0.1:8080
            (9:3)  GrpcExporterConfig
              (9:9)  name: second exporter
              (10:12)  address: localhost/127.0.0.1:8081
          (11:1)  pipelines:
            (12:3)  PipelineConfig:
              (12:9)  name: pipeline name
              (13:11)  signal: TRACE
              (14:9)  from: first receiver
              (15:10)  start:
                (15:10)  spanlet name
              (16:3)  processors:
                (17:5)  processorConfig:
                  (17:11)  name: spanlet name
                  (18:11)  kind: SPANLET
                  (19:9)  to:
                    (19:9)  first exporter
                  (20:11)  type: groovy-action
                  (21:5) config:
                    (21:5)  groovyActionConfig:
                      (22:15)  action: action-value""";

        assertEquals(expected, rawConfig.describe());


    }
}
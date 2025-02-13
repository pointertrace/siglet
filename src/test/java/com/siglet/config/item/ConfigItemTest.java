package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.located.Location;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.siglet.config.ConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class ConfigItemTest {

    private ConfigParser configParser;

    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
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
                  siglets:
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
        ConfigItem config = assertInstanceOf(ConfigItem.class, conf);

        List<ReceiverItem> receivers = config.getReceivers();
        assertNotNull(receivers);
        assertEquals(2, receivers.size());
        assertEquals(Location.of(1,1), config.getReceiversLocation());

        assertEquals("first receiver", receivers.getFirst().getName());
        assertEquals("second receiver", receivers.get(1).getName());

        List<ExporterItem> exporters = config.getExporters();
        assertNotNull(exporters);
        assertEquals(2, exporters.size());
        assertEquals(Location.of(6,1), config.getExportersLocation());

        assertEquals("first exporter", exporters.getFirst().getName());
        assertEquals("second exporter", exporters.get(1).getName());

        List<PipelineItem> pipelines = config.getPipelines();
        assertNotNull(pipelines);
        assertEquals(1, pipelines.size());
        assertEquals(Location.of(11,1), config.getPipelinesLocation());

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
                  siglets:
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
        ConfigItem config = assertInstanceOf(ConfigItem.class, conf);

        String expected = """
        (1:1)  config:
          (1:1)  receivers:
            (2:3)  GrpcReceiver
              (2:9)  name: first receiver
              (3:12)  address: localhost/<unresolved>:8080
            (4:3)  GrpcReceiver
              (4:9)  name: second receiver
              (5:12)  address: localhost/<unresolved>:8081
          (6:1)  exporters:
            (7:3)  GrpcExporter
              (7:9)  name: first exporter
              (8:12)  address: localhost/<unresolved>:8080
            (9:3)  GrpcExporter
              (9:9)  name: second exporter
              (10:12)  address: localhost/<unresolved>:8081
          (11:1)  pipelines:
            (12:3)  Pipeline:
              (12:9)  name: pipeline name
              (13:11)  signal: TRACE
              (14:9)  from: first receiver
              (15:10)  start:
                (15:10)  spanlet name
              (16:3)  siglets:
                (17:5)  siglet:
                  (17:11)  name: spanlet name
                  (18:11)  kind: SPANLET
                  (19:9)  to:
                    (19:9)  first exporter
                  (20:11)  type: groovy-action
                  (21:5) config:
                    (21:5)  processorConfig
                      (22:15)  action: action-value""";

        assertEquals(expected, config.describe());


    }
}
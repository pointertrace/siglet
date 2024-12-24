package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                - grpc: first-receiver
                  address: localhost:8080
                - grpc: second-receiver
                  address: localhost:8081
                exporters:
                - grpc: first-exporter
                  address: localhost:8080
                - grpc: second-exporter
                  address: localhost:8081
                pipelines:
                - trace: pipeline name
                  from: first-receiver
                  start: first-receiver
                  pipeline:
                  - spanlet: spanletItem-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        globalConfig.validateUniqueNames();

    }

    @Test
    void explain() throws Exception {


        var yaml = """
                receivers:
                - grpc: first-receiver
                  address: localhost:8080
                - grpc: second-receiver
                  address: localhost:8081
                exporters:
                - grpc: first-exporter
                  address: localhost:8080
                - grpc: second-exporter
                  address: localhost:8081
                pipelines:
                - trace: pipeline name
                  from: first-receiver
                  start: first-receiver
                  pipeline:
                  - spanlet: spanletItem-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        ConfigItem config = assertInstanceOf(ConfigItem.class, conf);

        String expected = """
                (1:1)  ConfigItem
                  (1:1)  receiverItems
                    (1:1)  arrayItem
                      (2:3)  array item
                        (2:3)  GrpcReceiverItem
                          (2:9)  name
                            (2:9)  String  (first-receiver)
                          (3:12)  address
                            (3:12)  InetSocketAddress  (localhost/<unresolved>:8080)

                      (4:3)  array item
                        (4:3)  GrpcReceiverItem
                          (4:9)  name
                            (4:9)  String  (second-receiver)
                          (5:12)  address
                            (5:12)  InetSocketAddress  (localhost/<unresolved>:8081)
                  (6:1)  exporterItems
                    (6:1)  arrayItem
                      (7:3)  array item
                        (7:3)  GrpcExporterItem
                          (7:9)  name
                            (7:9)  String  (first-exporter)
                          (8:12)  address
                            (8:12)  InetSocketAddress  (localhost/<unresolved>:8080)

                      (9:3)  array item
                        (9:3)  GrpcExporterItem
                          (9:9)  name
                            (9:9)  String  (second-exporter)
                          (10:12)  address
                            (10:12)  InetSocketAddress  (localhost/<unresolved>:8081)
                  (11:1)  pipelines
                    (11:1)  arrayItem
                      (12:3)  array item
                        (12:3)  PipelineItem
                          (12:10)  name
                            (12:10)  String  (pipeline name)
                          (13:9)  from
                            (13:9)  String  (first-receiver)
                          (14:10)  start
                            (14:10)  String  (first-receiver)
                          (15:3)  processors
                            (15:3)  arrayItem
                              (16:5)  array item
                                (16:5)  spanletItem
                                  (16:14)  name
                                    (16:14)  String  (spanletItem-name)
                                  (17:9)  to
                                    (17:9)  arrayItem
                                      (17:9)  array item
                                        (17:9)  String  (destination-value)
                                  (18:11)  type
                                    (18:11)  String  (processor)
                                  (19:5)  config
                                    (19:5)  processorConfig
                                      (20:15)  action
                                        (20:15)  String  (action-value)""";

        assertEquals(expected, config.describe());


    }
//    @Test
    public void validateUniqueNames_notUnique() throws Exception {


        var yaml = """
                receivers:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                exporters:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                pipelines:
                - trace: pipeline-name
                  from: first
                  start: first-destination
                  pipeline:
                  - spanlet: spanlet-name
                    to:
                    - first-destination
                    - second-destination
                    type: processor
                    config:
                      action: action-value
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                - trace: pipeline-name
                  from: second
                  start: spanlet-name
                  pipeline:
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                  - spanlet: spanletItem-name
                    to:
                    - first-destination
                    - second-destination
                    type: processor
                    config:
                      action: action-value
                """;


        Node node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        SigletError ex = assertThrowsExactly(SigletError.class, globalConfig::validateUniqueNames);

        assertEquals("Names must be unique within the configuration file but: 'pipeline-name' appears twice," +
                " 'spanletItem-name' appears 4 times, 'first' appears twice and 'second' appears twice!", ex.getMessage());

    }
}
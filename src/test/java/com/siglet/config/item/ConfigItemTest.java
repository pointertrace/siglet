package com.siglet.config.item;

import com.siglet.SigletError;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
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
    public void validateUniqueNames() throws Exception {


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


        ConfigNode node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        globalConfig.validateUniqueNames();

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


        ConfigNode node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        var globalConfig = assertInstanceOf(ConfigItem.class, conf);

        SigletError ex = assertThrowsExactly(SigletError.class, globalConfig::validateUniqueNames);

        assertEquals("Names must be unique within the configuration file but: 'pipeline-name' appears twice," +
                " 'spanletItem-name' appears 4 times, 'first' appears twice and 'second' appears twice!", ex.getMessage());

    }
}
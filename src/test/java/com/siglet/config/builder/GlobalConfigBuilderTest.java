package com.siglet.config.builder;

import com.siglet.SigletError;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.GlobalConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class GlobalConfigBuilderTest {

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
                  pipeline:
                  - spanlet: spanlet-name
                    from: origin-value
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                """;


        ConfigNode node = configParser.parse(yaml);

        globalConfigChecker().check(node);

        Object conf = node.getValue();

        assertNotNull(conf);
        var globalConfig = assertInstanceOf(GlobalConfigBuilder.class, conf);

        globalConfig.validateUniqueNames();

    }

    @Test
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
                  pipeline:
                  - spanlet: spanlet-name
                    from:
                    - first-origin
                    - second-origin
                    to:
                    - first-destination
                    - second-destination
                    type: processor
                    config:
                      action: action-value
                  - spanlet: spanlet-name
                    from: origin-value
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                - trace: pipeline-name
                  pipeline:
                  - spanlet: spanlet-name
                    from: origin-value
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                  - spanlet: spanlet-name
                    from:
                    - first-origin
                    - second-origin
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
        var globalConfig = assertInstanceOf(GlobalConfigBuilder.class, conf);

        SigletError ex = assertThrowsExactly(SigletError.class, globalConfig::validateUniqueNames);

        assertEquals("Names must be unique within the configuration file but: 'pipeline-name' appears twice," +
                " 'spanlet-name' appears 4 times, 'first' appears twice and 'second' appears twice!", ex.getMessage());

    }
}
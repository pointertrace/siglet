package com.siglet.config.builder;

import com.siglet.SigletError;
import com.siglet.config.parser.ConfigParser;
import com.siglet.config.parser.node.ConfigNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.siglet.config.GlobalConfigCheckFactory.globalConfigChecker;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionValidatorTest {

    private ConfigParser configParser;

    private ConnectionValidator connectionValidator;


    @BeforeEach
    public void setUp() {
        configParser = new ConfigParser();
        connectionValidator = new ConnectionValidator();
    }

    @Test
    public void parse() throws Exception {


        var yaml = """
                exporters:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                receivers:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                pipelines:
                - trace: pipeline name
                  start: first
                  pipeline:
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                - trace: pipeline name
                  start: second
                  pipeline:
                  - spanlet: spanlet-name
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

         connectionValidator.validate(globalConfig);

    }

    @Test
    public void parse_sameName() throws Exception {


        var yaml = """
                exporters:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                receivers:
                - grpc: first
                  address: localhost:8080
                - grpc: second
                  address: localhost:8081
                - grpc: third
                  address: localhost:8081
                pipelines:
                - trace: pipeline-name
                  start: first
                  pipeline:
                  - spanlet: spanlet-name
                    to: destination-value
                    type: processor
                    config:
                      action: action-value
                  - spanlet: spanlet-name
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

        SigletError ex = assertThrowsExactly(SigletError.class, () -> connectionValidator.validate(globalConfig));

        assertEquals("Receiver(s) second and third is(are) not being used in any pipeline!", ex.getMessage());

    }
}
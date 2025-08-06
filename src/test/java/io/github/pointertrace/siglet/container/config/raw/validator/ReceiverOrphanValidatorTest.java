package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceiverOrphanValidatorTest {

    private ConfigFactory configFactory;

    private ReceiverOrphanValidator receiverOrphanValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        receiverOrphanValidator = new ReceiverOrphanValidator();

    }

    @Test
    void validate_receiverOrphan() {

        String configTxt = """
                receivers:
                - debug: receiver
                - debug: orphan-receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig =  configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> receiverOrphanValidator.Validate(rawConfig));

        assertEquals("""
                The following receivers are orphaned:
                    [orphan-receiver] at (3,3)""", e.getMessage());

    }

    @Test
    void validate() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig =  configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        receiverOrphanValidator.Validate(rawConfig);

    }
}
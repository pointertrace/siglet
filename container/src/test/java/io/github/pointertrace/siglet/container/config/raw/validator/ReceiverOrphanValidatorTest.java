package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverTypeRegistry;
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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> receiverOrphanValidator.validate(rawConfig));

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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry());

        receiverOrphanValidator.validate(rawConfig);

    }
}
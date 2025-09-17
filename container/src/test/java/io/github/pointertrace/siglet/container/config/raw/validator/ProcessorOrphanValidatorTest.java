package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorOrphanValidatorTest {

    private ConfigFactory configFactory;

    private ProcessorOrphanValidator processorOrphanValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        processorOrphanValidator = new ProcessorOrphanValidator();

    }

    @Test
    void validate_processorOrphan() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - debug: orphan-exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: orphan-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorOrphanValidator.validate(rawConfig));

        assertEquals("""
                The following processors are orphaned:
                    [orphan-spanlet] at (15,5)""", e.getMessage());

    }

    @Test
    void validate() {
        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: other-pipeline
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
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

        processorOrphanValidator.validate(rawConfig);

    }

    @Test
    void validate_alias() {
        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: alias:other-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry());

        processorOrphanValidator.validate(rawConfig);

    }

}
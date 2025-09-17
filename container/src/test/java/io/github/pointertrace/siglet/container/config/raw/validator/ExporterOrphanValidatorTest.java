package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExporterOrphanValidatorTest {

    private ConfigFactory configFactory;

    private ExporterOrphanValidator exporterOrphanValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        exporterOrphanValidator = new ExporterOrphanValidator();

    }

    @Test
    void validate_exporterOrphan() {

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
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> exporterOrphanValidator.validate(rawConfig));

        assertEquals("""
                The following exporters are orphaned:
                    [orphan-exporter] at (5,3)""", e.getMessage());

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

        exporterOrphanValidator.validate(rawConfig);

    }

    @Test
    void validate_alias() {
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
                    to: alias:exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry());

        exporterOrphanValidator.validate(rawConfig);

    }
}
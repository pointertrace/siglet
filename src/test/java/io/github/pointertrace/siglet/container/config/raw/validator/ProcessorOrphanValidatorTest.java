package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
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
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: orphan-spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorOrphanValidator.Validate(rawConfig));

        assertEquals("""
                The following processors are orphaned:
                    [orphan-spanlet] at (17,5)""", e.getMessage());

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
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
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

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        processorOrphanValidator.Validate(rawConfig);

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:other-spanlet
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: other-spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        processorOrphanValidator.Validate(rawConfig);

    }

}
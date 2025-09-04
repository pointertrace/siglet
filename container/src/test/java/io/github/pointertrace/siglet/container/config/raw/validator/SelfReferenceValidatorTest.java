package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelfReferenceValidatorTest {

    private ConfigFactory configFactory;

    private SelfReferenceValidator selfReferenceValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        selfReferenceValidator = new SelfReferenceValidator();

    }

    @Test
    void validate_selfReference() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start:
                  - spanlet
                  - pipeline
                  processors:
                  - spanlet-groovy-action: spanlet
                    to:
                    - exporter
                    - spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> selfReferenceValidator.validate(rawConfig));

        assertEquals("""
                The following items have a self reference:
                    pipeline [pipeline] at (6,3)
                    processor [spanlet] at (12,5)""", e.getMessage());
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
                  start:
                  - spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to:
                    - exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

         selfReferenceValidator.validate(rawConfig);

    }
}
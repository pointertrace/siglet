package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UniqueNameValidatorTest {

    private ConfigFactory configFactory;

    private UniqueNameValidator uniqueNameValidator;


    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        uniqueNameValidator = new UniqueNameValidator();

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

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        uniqueNameValidator.validate(rawConfig);

    }

    @Test
    void validate_validationError() {

        String configTxt = """
                receivers:
                - debug: receiver
                - debug: name
                exporters:
                - debug: exporter
                - debug: name
                pipelines:
                - name: name
                  from: receiver
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: name
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


        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> uniqueNameValidator.validate(rawConfig));

        assertEquals(
                """
                        Configuration items must have a unique name but The following items have the same name:
                            receiver [name] at (3,3)
                            exporter [name] at (6,3)
                            pipeline [name] at (8,3)
                            processor [name] at (12,5)""", e.getMessage());

    }


}
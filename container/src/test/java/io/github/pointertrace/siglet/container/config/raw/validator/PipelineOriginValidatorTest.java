package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineOriginValidatorTest {

    private ConfigFactory configFactory;

    private PipelineOriginValidator pipelineOriginValidator;


    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        pipelineOriginValidator = new PipelineOriginValidator();

    }

    @Test
    void validate_originNotFound() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: non-existing
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig =  configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> pipelineOriginValidator.validate(rawConfig));

        assertEquals("Pipeline [pipeline] at (6,3) has [non-existing] as origin and there is no receiver " +
                     "with that name.", e.getMessage());

    }


    @Test
    void validate_originIsNotReceiver() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: spanlet
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig =  configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> pipelineOriginValidator.validate(rawConfig));

        assertEquals("Pipeline [pipeline] at (6,3) has processor [spanlet] as origin and it should be a " +
                     "receiver.", e.getMessage());

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

        pipelineOriginValidator.validate(rawConfig);

    }

    @Test
    void validate_withoutFrom() {

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
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        pipelineOriginValidator.validate(rawConfig);

    }
}
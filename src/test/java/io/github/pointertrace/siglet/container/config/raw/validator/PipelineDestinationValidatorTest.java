package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineDestinationValidatorTest {

    private ConfigFactory configFactory;

    private PipelineDestinationValidator pipelineDestinationValidator;


    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        pipelineDestinationValidator = new PipelineDestinationValidator();

    }

    @Test
    void validate_destinationNotFound() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: non-existing
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> pipelineDestinationValidator.validate(rawConfig));

        assertEquals("Pipeline [pipeline] at (6,3) has [non-existing] as destination and there is no processor " +
                     "or exporter with that name.", e.getMessage());
    }


    @Test
    void validate_destinationIsNotReceiver() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: spanlet
                  start: receiver
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> pipelineDestinationValidator.validate(rawConfig));

        assertEquals("Pipeline [pipeline] at (6,3) has receiver [receiver] as destination and it should be a " +
                     "processor or an exporter.", e.getMessage());

    }

    @Test
    void validate_toProcessor() {

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

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        pipelineDestinationValidator.validate(rawConfig);

    }

    @Test
    void validate_toExporter() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: exporter
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        pipelineDestinationValidator.validate(rawConfig);

    }
}
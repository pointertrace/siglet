package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessorDestinationValidatorTest {

    private ConfigFactory configFactory;

    private ProcessorDestinationValidator processorDestinationValidator;


    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        processorDestinationValidator = new ProcessorDestinationValidator();

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
                    to: non-existing
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.Validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor or exporter with that name.", e.getMessage());

    }

    @Test
    void validate_destinationAliasNotFound() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: internal:non-existing
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: alias:non-existing
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.Validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor or exporter with that name.", e.getMessage());

    }

    @Test
    void validate_destinationIsNotProcessorOrExporter() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: spanlet
                  start: exporter
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: pipeline
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.Validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has pipeline [pipeline] as destination and it should be " +
                     "a processor or a exporter.", e.getMessage());

    }

    @Test
    void validate_destinationAliasIsNotProcessorOrExporter() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: spanlet
                  start: exporter
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: alias:receiver
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.Validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has receiver [receiver] as destination and it should be " +
                     "a processor or a exporter.", e.getMessage());

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

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        processorDestinationValidator.Validate(rawConfig);

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
                  from: receiver
                  start: spanlet
                  processors:
                  - name: spanlet
                    kind: spanlet
                    to: alias:exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        processorDestinationValidator.Validate(rawConfig);

    }
}
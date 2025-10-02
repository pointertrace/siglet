package io.github.pointertrace.siglet.impl.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.ConfigFactory;
import io.github.pointertrace.siglet.impl.config.raw.RawConfig;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());

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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());

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

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        pipelineDestinationValidator.validate(rawConfig);

    }
}
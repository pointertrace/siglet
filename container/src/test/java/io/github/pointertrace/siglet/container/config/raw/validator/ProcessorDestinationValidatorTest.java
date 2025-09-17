package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.container.engine.receiver.ReceiverTypeRegistry;
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
                  - spanlet-groovy-action: spanlet
                    to: non-existing
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor, exporter or pipeline with that name.", e.getMessage());

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
                  - spanlet-groovy-action: spanlet
                    to: alias:non-existing
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor, exporter or pipeline with that name.", e.getMessage());

    }

    @Test
    void validate_destinationIsNotProcessorOrExporterOrPipeline() {

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
                  - spanlet-groovy-action: spanlet
                    to: receiver
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has receiver [receiver] as destination and it should be " +
                     "a processor, exporter or pipeline.", e.getMessage());

    }

    @Test
    void validate_destinationAliasIsNotProcessorOrExporterOrPipeline() {

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
                  - spanlet-groovy-action: spanlet
                    to: alias:receiver
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(rawConfig));

        assertEquals("Processor [spanlet] at (10,5) has receiver [receiver] as destination and it should be " +
                     "a processor, exporter or pipeline.", e.getMessage());

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
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }

    @Test
    void validate_toExporterAlias() {

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
                    to: alias:exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

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
                    to: other-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }

    @Test
    void validate_toProcessorAlias() {

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
                    to: alias:other-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }

    @Test
    void validate_toPipeline() {

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
                    to: other-pipeline
                    config:
                      action: signal.name = signal.name +"-suffix"
                - name: other-pipeline
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }

    @Test
    void validate_toPipelineAlias() {

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
                    to: alias:other-pipeline
                    config:
                      action: signal.name = signal.name +"-suffix"
                - name: other-pipeline
                  start: spanlet
                  processors:
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ReceiverTypeRegistry(),
                new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

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
                  - spanlet-groovy-action: spanlet
                    to: alias:exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt,
                new ReceiverTypeRegistry(), new ProcessorTypeRegistry(), new ExporterTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }
}
package io.github.pointertrace.siglet.container.config.raw.validator;

import io.github.pointertrace.siglet.container.SigletError;
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
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:non-existing
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: receiver
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:receiver
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: other-spanlet
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: other-spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:other-spanlet
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: other-spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: other-pipeline
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                - name: other-pipeline
                  start: spanlet
                  processors:
                  - name: other-spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:other-pipeline
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                - name: other-pipeline
                  start: spanlet
                  processors:
                  - name: other-spanlet
                    kind: spanlet
                    to: exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

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
                  - name: spanlet
                    kind: spanlet
                    to: alias:exporter
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        RawConfig rawConfig = configFactory.createRawConfig(configTxt, new ProcessorTypeRegistry());

        processorDestinationValidator.validate(rawConfig);

    }
}
package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.ConfigFactory;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessorDestinationValidatorTest {

    private ProcessorDestinationValidator processorDestinationValidator;

    @BeforeEach
    void setUp() {

        processorDestinationValidator = new ProcessorDestinationValidator();

    }

    @Test
    void validate_destinationNotFound() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(yamlDescriptor));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor, exporter or pipeline with that name.", e.getMessage());

    }

    @Test
    void validate_destinationAliasNotFound() {

        String yaml = """
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
                    to: alias:non-existing
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(yamlDescriptor));

        assertEquals("Processor [spanlet] at (10,5) has [non-existing] as destination and there is no " +
                     "processor, exporter or pipeline with that name.", e.getMessage());

    }

    @Test
    void validate_destinationIsNotProcessorOrExporterOrPipeline() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(yamlDescriptor));

        assertEquals("Processor [spanlet] at (10,5) has receiver [receiver] as destination and it should be " +
                     "a processor, exporter or pipeline.", e.getMessage());

    }

    @Test
    void validate_destinationAliasIsNotProcessorOrExporterOrPipeline() {

        String yaml = """
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
                    to: alias:receiver
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> processorDestinationValidator.validate(yamlDescriptor));

        assertEquals("Processor [spanlet] at (10,5) has receiver [receiver] as destination and it should be " +
                     "a processor, exporter or pipeline.", e.getMessage());

    }


    @Test
    void validate_toExporter() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_toExporterAlias() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_toProcessor() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_toProcessorAlias() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_toPipeline() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_toPipelineAlias() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_alias() {

        String yaml = """
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

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorDestinationValidator.validate(yamlDescriptor);

    }
}
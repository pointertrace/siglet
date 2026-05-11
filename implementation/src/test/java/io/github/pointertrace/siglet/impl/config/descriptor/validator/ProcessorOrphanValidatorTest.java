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

class ProcessorOrphanValidatorTest {

    private ProcessorOrphanValidator processorOrphanValidator;

    @BeforeEach
    void setUp() {

        processorOrphanValidator = new ProcessorOrphanValidator();

    }

    @Test
    void validate_processorOrphan() {

        String yaml = """
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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: orphan-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> processorOrphanValidator.validate(yamlDescriptor));

        assertEquals(
                """
                        The following processors are orphaned:
                            [orphan-spanlet] at (15,5)""", e.getMessage());

    }

    @Test
    void validate() {

        String yaml = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: other-pipeline
                  from: receiver
                  start: spanlet
                  processors:
                  # processor pointed by pipeline
                  - spanlet-groovy-action: spanlet
                    to: other-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  # processor pointed by other processor
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

       YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorOrphanValidator.validate(yamlDescriptor);

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
                    to: alias:other-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: other-spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        processorOrphanValidator.validate(yamlDescriptor);

    }

}
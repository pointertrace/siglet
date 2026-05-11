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

class SelfReferenceValidatorTest {

    private SelfReferenceValidator selfReferenceValidator;


    private YamlDescriptor yamlDescriptor;

    @BeforeEach
    void setUp() {

        selfReferenceValidator = new SelfReferenceValidator();

    }

    @Test
    void validate_selfReference() {

        String yaml = """
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
                    - spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> selfReferenceValidator.validate(yamlDescriptor));

        assertEquals("""
                The following items have a self reference:
                    processor [spanlet] at (11,5)""", e.getMessage());
    }

    @Test
    void validate() {

        String yaml = """
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

        yamlDescriptor = YamlDescriptor.parse(yaml);
        selfReferenceValidator.validate(yamlDescriptor);

    }
}
package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UniqueNameValidatorTest {

    private UniqueNameValidator uniqueNameValidator;



    @BeforeEach
    void setUp() {

        uniqueNameValidator = new UniqueNameValidator();

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


        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        uniqueNameValidator.validate(yamlDescriptor);

    }

    @Test
    void validate_validationError() {

        String yaml = """
                receivers:
                - debug: receiver
                - debug: name
                exporters:
                - debug: exporter
                - debug: name
                pipelines:
                - name: name
                  from: receiver
                  start:
                  - spanlet
                  processors:
                  - spanlet-groovy-action: name
                    to:
                    - exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                - name: pipeline
                  from: receiverDescriptor
                  start:
                  - spanlet
                  processors:
                  - spanlet-groovy-action: name
                    to:
                    - exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;


        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> uniqueNameValidator.validate(yamlDescriptor));

        assertEquals(
                """
                        Configuration items must have a unique name but The following items have the same name:
                            pipeline [name] at (8,3)
                            exporter [name] at (6,3)
                            receiver [name] at (3,3)
                            processor [name] at (13,5)""", e.getMessage());
    }


}
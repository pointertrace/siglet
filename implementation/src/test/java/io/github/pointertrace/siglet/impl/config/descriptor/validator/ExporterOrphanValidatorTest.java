package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExporterOrphanValidatorTest {

    private ExporterOrphanValidator exporterOrphanValidator;

    @BeforeEach
    void setUp() {

        exporterOrphanValidator = new ExporterOrphanValidator();

    }

    @Test
    void validate_exporterOrphan() {

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
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        SigletError e = assertThrows(SigletError.class, () -> exporterOrphanValidator.validate(yamlDescriptor));

        assertEquals("""
                The following exporters are orphaned:
                    [orphan-exporter] at (5,3)""", e.getMessage());

    }

    @Test
    void validate_internalMetricsExporter() {

        String yaml = """
                global:
                  internal-metrics-grpc-exporter: orphan-exporter
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - grpc: orphan-exporter
                  config:
                    address: localhost:4317
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

         exporterOrphanValidator.validate(yamlDescriptor);

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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
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

        assertDoesNotThrow(() -> exporterOrphanValidator.validate(yamlDescriptor));

    }

    @Test
    void validate_alias() {

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
                  - spanlet-groovy-action: spanlet
                    to: exporter
                    config:
                      action: signal.name = signal.name +"-suffix"
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

        assertDoesNotThrow(() -> exporterOrphanValidator.validate(yamlDescriptor));


    }
}
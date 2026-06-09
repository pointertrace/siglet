package io.github.pointertrace.siglet.impl.config.descriptor.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InternalMetricsExporterValidatorTest {

    private InternalMetricsExporterValidator internalMetricsExporterValidator;

    @BeforeEach
    void setUp() {

        internalMetricsExporterValidator = new InternalMetricsExporterValidator();

    }

    @Test
    void validate_noExporter() {

        String yaml = """
                global:
                  internal-metrics-exporter: internal-metrics
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

        SigletError e = assertThrows(SigletError.class, () -> internalMetricsExporterValidator.validate(yamlDescriptor));

        assertEquals("Cannot find exporter [internal-metrics] for internal metrics exporter", e.getMessage());

    }

    @Test
    void validate() {

        String yaml = """
                global:
                  internal-metrics-exporter: internal-metrics
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - debug: internal-metrics
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

        assertDoesNotThrow(() -> internalMetricsExporterValidator.validate(yamlDescriptor));

    }

}
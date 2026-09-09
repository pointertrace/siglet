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
    void validate_exporterNotGrpc() {

        String yaml = """
                global:
                  internal-metrics-grpc-exporter: internal-metrics-exporter
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - debug: internal-metrics-exporter
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

        assertEquals("The internal-metrics-grpc-exporter [internal-metrics-exporter] at (7,3) " +
                "must be a grpc exporter", e.getMessage());

    }

    @Test
    void validate_notAndExport() {

        String yaml = """
                global:
                  internal-metrics-grpc-exporter: receiver
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - debug: internal-metrics-exporter
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

        assertEquals("The internal-metrics-grpc-exporter [receiver] is not defined in the exporters section",
                e.getMessage());

    }

    @Test
    void validate_exporterNotFound() {

        String yaml = """
                global:
                  internal-metrics-grpc-exporter: inexistent
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - debug: internal-metrics-exporter
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

        assertEquals("The internal-metrics-grpc-exporter [inexistent] is not defined in the exporters section",
                e.getMessage());

    }
    @Test
    void validate() {

        String yaml = """
                global:
                  internal-metrics-grpc-exporter: internal-metrics-exporter
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                - grpc: internal-metrics-exporter
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

        internalMetricsExporterValidator.validate(yamlDescriptor);

    }


}
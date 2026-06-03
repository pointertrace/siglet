package io.github.pointertrace.siglet.impl.config.descriptor;

import io.github.pointertrace.siglet.impl.engine.exporter.ExporterTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.pipeline.processor.ProcessorTypeRegistry;
import io.github.pointertrace.siglet.impl.engine.receiver.ReceiverTypeRegistry;
import io.github.pointertrace.siglet.parser.Node;
import io.github.pointertrace.siglet.parser.Parser;
import io.github.pointertrace.siglet.parser.Schema;
import io.github.pointertrace.siglet.parser.SchemaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MessageTest {


    @Test
    public void test() {

        String expected = """
                Error in pipeline configuration:
                  Error in exporters at (3,1):
                    Error in exporters at (4,1):
                      Error in exporters array item at (4,3):
                        Error in exporter at (4,3):
                          Error in config at (5,3):
                            Error in configuration object at (6,5):
                              Invalid property""";


        String yaml = """
                receivers:
                  - debug: receiver
                exporters:
                  - grpc: exporter
                    config:
                      address: localhost:8080
                      invalid-property: value
                pipelines:
                  - name: pipeline
                    from: receiver
                    start: spanlet
                    processors:
                      - spanlet-groovy-action: spanlet
                        to: exporter
                        config:
                          action: signal.name = signal.name +"-suffix" """;

        Node node = Parser.DEFAULT.parse(yaml);

        Schema schema = YamlDescriptor.descriptorSchemaBuilder(new ReceiverTypeRegistry(), new ProcessorTypeRegistry(),
                new ExporterTypeRegistry()).build();


        System.out.println(schema.describe(2));
        SchemaException e = assertThrows(SchemaException.class, () -> schema.validate(node));

        assertEquals(expected, e.getMessage());

    }
}

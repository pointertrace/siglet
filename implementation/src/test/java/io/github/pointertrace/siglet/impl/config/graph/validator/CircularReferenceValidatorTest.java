package io.github.pointertrace.siglet.impl.config.graph.validator;

import io.github.pointertrace.siglet.api.SigletError;
import io.github.pointertrace.siglet.impl.config.descriptor.YamlDescriptor;
import io.github.pointertrace.siglet.impl.config.graph.Graph;
import io.github.pointertrace.siglet.impl.config.graph.GraphFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircularReferenceValidatorTest {

    private GraphFactory graphFactory;

    private CircularReferenceValidator circularReferenceValidator;

    @BeforeEach
    void setUp() {

        graphFactory = new GraphFactory();

        circularReferenceValidator = new CircularReferenceValidator();

    }

    @Test
    void validate_circularReferenceProcessorToProcessor() {

        String yaml = """
                receivers:
                - debug: receiver
                pipelines:
                - name: pipeline
                  from: receiver
                  start: first-spanlet
                  processors:
                  - spanlet-groovy-action: first-spanlet
                    to: second-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: second-spanlet
                    to: first-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        Graph graph = graphFactory.create(yamlDescriptor);

        SigletError e = assertThrows(SigletError.class, () -> circularReferenceValidator.validate(graph));

        assertEquals("""
                        There are circular references:
                            -> pipeline [pipeline] at (4,3)-> processor [first-spanlet] at (8,5)-> processor [second-spanlet] at (12,5)-> processor [first-spanlet] at (8,5)""",
                e.getMessage());

    }

    @Test
    void validate_circularReferenceProcessorToPipeline() {

        String yaml = """
                receivers:
                - debug: receiver
                pipelines:
                - name: pipeline
                  from: receiver
                  start: first-spanlet
                  processors:
                  - spanlet-groovy-action: first-spanlet
                    to: second-spanlet
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - spanlet-groovy-action: second-spanlet
                    to: pipeline
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        Graph graph = graphFactory.create(yamlDescriptor);

        SigletError e = assertThrows(SigletError.class, () -> circularReferenceValidator.validate(graph));


        assertEquals("""
                       There are circular references:
                           -> pipeline [pipeline] at (4,3)-> processor [first-spanlet] at (8,5)-> processor [second-spanlet] at (12,5)-> pipeline [pipeline] at (4,3)""",
                e.getMessage());

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
                """;

        YamlDescriptor yamlDescriptor = YamlDescriptor.parse(yaml);

        Graph graph = graphFactory.create(yamlDescriptor);

        circularReferenceValidator.validate(graph);

    }

}
package io.github.pointertrace.siglet.container.config.graph.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.graph.Graph;
import io.github.pointertrace.siglet.container.config.graph.GraphFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.config.raw.validator.ExporterOrphanValidator;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularReferenceValidatorTest {

    private ConfigFactory configFactory;

    private GraphFactory graphFactory;

    private CircularReferenceValidator circularReferenceValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        graphFactory = new GraphFactory();

        circularReferenceValidator = new CircularReferenceValidator();

    }

    @Test
    void validate_circularReferenceProcessorToProcessor() {

        String configTxt = """
                receivers:
                - debug: receiver
                pipelines:
                - name: pipeline
                  from: receiver
                  start: first-spanlet
                  processors:
                  - name: first-spanlet
                    kind: spanlet
                    to: second-spanlet
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: second-spanlet
                    kind: spanlet
                    to: first-spanlet
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        Config config = configFactory.create(configTxt);

        Graph graph = graphFactory.create(config);

        SigletError e = assertThrows(SigletError.class, () -> circularReferenceValidator.validate(graph));

        assertEquals("""
                        There are circular references:
                            -> pipeline [pipeline] at (4,3)-> processor [first-spanlet] at (8,5)-> processor [second-spanlet] at (14,5)-> processor [first-spanlet] at (8,5)""",
                e.getMessage());

    }

    @Test
    void validate_circularReferenceProcessorToPipeline() {

        String configTxt = """
                receivers:
                - debug: receiver
                pipelines:
                - name: pipeline
                  from: receiver
                  start: first-spanlet
                  processors:
                  - name: first-spanlet
                    kind: spanlet
                    to: second-spanlet
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: second-spanlet
                    kind: spanlet
                    to: pipeline
                    type: spanlet-groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        Config config = configFactory.create(configTxt);

        Graph graph = graphFactory.create(config);

        SigletError e = assertThrows(SigletError.class, () -> circularReferenceValidator.validate(graph));

        assertEquals("""
                       There are circular references:
                           -> pipeline [pipeline] at (4,3)-> processor [first-spanlet] at (8,5)-> processor [second-spanlet] at (14,5)-> pipeline [pipeline] at (4,3)""",
                e.getMessage());

    }

    @Test
    void validate() {
        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: other-pipeline
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

        Config config = configFactory.create(configTxt);

        Graph graph = graphFactory.create(config);

        circularReferenceValidator.validate(graph);

    }

}
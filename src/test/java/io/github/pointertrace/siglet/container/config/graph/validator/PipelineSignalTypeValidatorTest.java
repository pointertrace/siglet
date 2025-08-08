package io.github.pointertrace.siglet.container.config.graph.validator;

import io.github.pointertrace.siglet.container.SigletError;
import io.github.pointertrace.siglet.container.config.Config;
import io.github.pointertrace.siglet.container.config.ConfigFactory;
import io.github.pointertrace.siglet.container.config.graph.Graph;
import io.github.pointertrace.siglet.container.config.graph.GraphFactory;
import io.github.pointertrace.siglet.container.config.raw.RawConfig;
import io.github.pointertrace.siglet.container.engine.pipeline.processor.ProcessorTypeRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PipelineSignalTypeValidatorTest {

    private ConfigFactory configFactory;

    private GraphFactory graphFactory;

    private PipelineSignalTypeValidator pipelineSignalTypeValidator;

    @BeforeEach
    void setUp() {

        configFactory = new ConfigFactory();

        graphFactory = new GraphFactory();

        pipelineSignalTypeValidator = new PipelineSignalTypeValidator();

    }

    @Test
    void validate_multipleSignalTypesPipeline() {

        String configTxt = """
                receivers:
                - debug: receiver
                exporters:
                - debug: exporter
                pipelines:
                - name: pipeline
                  from: receiver
                  start: first-spanlet
                  processors:
                  - name: first-spanlet
                    kind: spanlet
                    to: second-spanlet
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: second-spanlet
                    kind: spanlet
                    to: first-metriclet
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: first-metriclet
                    kind: metriclet
                    to: second-metriclet
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: second-metriclet
                    kind: metriclet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        Config config = configFactory.create(configTxt);

        Graph graph = graphFactory.create(config);

        SigletError e = assertThrows(SigletError.class, () -> pipelineSignalTypeValidator.validate(graph));

        assertTrue(e.getMessage().contains("Pipeline [pipeline] at (6,3) has multiple signal types:"));
        assertTrue(e.getMessage().contains("    SPAN: [first-spanlet] at (10,5), [second-spanlet] at (16,5)"));
        assertTrue(e.getMessage().contains("    METRIC: [first-metriclet] at (22,5), [second-metriclet] at (28,5)"));

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
                  start: first-spanlet
                  processors:
                  - name: first-spanlet
                    kind: spanlet
                    to: second-spanlet
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                  - name: second-spanlet
                    kind: spanlet
                    to: exporter
                    type: groovy-action
                    config:
                      action: signal.name = signal.name +"-suffix"
                """;

        Config config = configFactory.create(configTxt);

        Graph graph = graphFactory.create(config);

        pipelineSignalTypeValidator.validate(graph);

    }

}